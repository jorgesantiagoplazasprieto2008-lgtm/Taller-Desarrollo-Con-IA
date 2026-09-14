package com.entrecopas.financiero.service;

import com.entrecopas.exception.ResourceNotFoundException;
import com.entrecopas.financiero.dto.DashboardFinancieroDTO;
import com.entrecopas.financiero.dto.DesgloseLoteFinancieroDTO;
import com.entrecopas.financiero.dto.DesgloseProductoFinancieroDTO;
import com.entrecopas.financiero.dto.FinancieroKPIsDTO;
import com.entrecopas.lote.model.Lote;
import com.entrecopas.lote.repository.LoteRepository;
import com.entrecopas.producto.model.Producto;
import com.entrecopas.producto.repository.ProductoRepository;
import com.entrecopas.productor.model.Productor;
import com.entrecopas.productor.repository.ProductorRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de negocio para el cálculo de métricas financieras, costos y mermas del productor.
 * Aplica aislamiento multi-tenant estricto sobre los datos contables.
 */
@Service
public class FinancieroService {

    private final ProductorRepository productorRepository;
    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;

    public FinancieroService(ProductorRepository productorRepository,
                             ProductoRepository productoRepository,
                             LoteRepository loteRepository) {
        this.productorRepository = productorRepository;
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
    }

    /**
     * Calcula y consolida el dashboard financiero del productor autenticado.
     */
    @Transactional(readOnly = true)
    public DashboardFinancieroDTO obtenerDashboard(Long usuarioId) {
        return obtenerDashboard(usuarioId, null, null, null);
    }

    /**
     * Calcula y consolida el dashboard financiero del productor con filtros opcionales.
     */
    @Transactional(readOnly = true)
    public DashboardFinancieroDTO obtenerDashboard(Long usuarioId, Long productoId,
                                                   java.time.LocalDate fechaDesde,
                                                   java.time.LocalDate fechaHasta) {
        Productor productor = productorRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró una bodega o taller artesanal asociado a este usuario."));

        List<Producto> productos = productoRepository.findByProductorId(productor.getId());
        List<Lote> todosLotes = loteRepository.findByProductoProductorIdOrderByFechaProduccionDesc(productor.getId());

        List<Lote> lotes = todosLotes.stream()
                .filter(l -> productoId == null || (l.getProducto() != null && l.getProducto().getId().equals(productoId)))
                .filter(l -> fechaDesde == null || (l.getFechaProduccion() != null && !l.getFechaProduccion().isBefore(fechaDesde)))
                .filter(l -> fechaHasta == null || (l.getFechaProduccion() != null && !l.getFechaProduccion().isAfter(fechaHasta)))
                .collect(Collectors.toList());

        if (lotes.isEmpty()) {
            return new DashboardFinancieroDTO();
        }

        BigDecimal costoTotalAcumulado = BigDecimal.ZERO;
        BigDecimal volumenTotalLitros = BigDecimal.ZERO;
        BigDecimal mermaTotalLitros = BigDecimal.ZERO;

        List<DesgloseLoteFinancieroDTO> desgloseLotes = new ArrayList<>();

        for (Lote lote : lotes) {
            Producto prod = lote.getProducto();
            BigDecimal vol = lote.getVolumenLitros() != null ? lote.getVolumenLitros() : BigDecimal.ZERO;
            BigDecimal merma = lote.getMermaLitros() != null ? lote.getMermaLitros() : BigDecimal.ZERO;
            BigDecimal costo = lote.getCostoTotal() != null ? lote.getCostoTotal() : BigDecimal.ZERO;
            BigDecimal volNeto = vol.subtract(merma).max(BigDecimal.ZERO);

            costoTotalAcumulado = costoTotalAcumulado.add(costo);
            volumenTotalLitros = volumenTotalLitros.add(vol);
            mermaTotalLitros = mermaTotalLitros.add(merma);

            BigDecimal mermaPct = vol.compareTo(BigDecimal.ZERO) > 0
                    ? merma.multiply(BigDecimal.valueOf(100)).divide(vol, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            BigDecimal costoPorLitro = volNeto.compareTo(BigDecimal.ZERO) > 0
                    ? costo.divide(volNeto, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            double litrosPorUnidad = 0.75;
            if (prod != null && prod.getPresentacion() != null) {
                String pres = prod.getPresentacion().toLowerCase();
                if (pres.contains("330")) {
                    litrosPorUnidad = 0.33;
                } else if (pres.contains("500")) {
                    litrosPorUnidad = 0.50;
                }
            }

            int unidadesEstimadas = (int) (volNeto.doubleValue() / litrosPorUnidad);
            BigDecimal costoPorUnidad = unidadesEstimadas > 0
                    ? costo.divide(BigDecimal.valueOf(unidadesEstimadas), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            BigDecimal precioVenta = prod != null && prod.getPrecio() != null ? prod.getPrecio() : BigDecimal.ZERO;
            BigDecimal ingresoBruto = precioVenta.multiply(BigDecimal.valueOf(unidadesEstimadas));
            BigDecimal gananciaBruta = ingresoBruto.subtract(costo);

            BigDecimal margenBrutoPct = ingresoBruto.compareTo(BigDecimal.ZERO) > 0
                    ? gananciaBruta.multiply(BigDecimal.valueOf(100)).divide(ingresoBruto, 1, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            desgloseLotes.add(new DesgloseLoteFinancieroDTO(
                    lote.getId(),
                    lote.getCodigoTrazabilidad(),
                    prod != null ? prod.getNombre() : "Bebida Artesanal",
                    prod != null ? prod.getPresentacion() : "750ml",
                    lote.getFechaProduccion(),
                    vol.setScale(2, RoundingMode.HALF_UP),
                    merma.setScale(2, RoundingMode.HALF_UP),
                    mermaPct,
                    volNeto.setScale(2, RoundingMode.HALF_UP),
                    costo.setScale(2, RoundingMode.HALF_UP),
                    costoPorLitro,
                    unidadesEstimadas,
                    costoPorUnidad,
                    precioVenta.setScale(2, RoundingMode.HALF_UP),
                    margenBrutoPct
            ));
        }

        BigDecimal volumenNetoTotal = volumenTotalLitros.subtract(mermaTotalLitros).max(BigDecimal.ZERO);
        BigDecimal porcentajeMermaGlobal = volumenTotalLitros.compareTo(BigDecimal.ZERO) > 0
                ? mermaTotalLitros.multiply(BigDecimal.valueOf(100)).divide(volumenTotalLitros, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal costoPromedioLitro = volumenNetoTotal.compareTo(BigDecimal.ZERO) > 0
                ? costoTotalAcumulado.divide(volumenNetoTotal, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        FinancieroKPIsDTO kpis = new FinancieroKPIsDTO(
                costoTotalAcumulado.setScale(2, RoundingMode.HALF_UP),
                volumenTotalLitros.setScale(2, RoundingMode.HALF_UP),
                mermaTotalLitros.setScale(2, RoundingMode.HALF_UP),
                volumenNetoTotal.setScale(2, RoundingMode.HALF_UP),
                porcentajeMermaGlobal,
                costoPromedioLitro,
                lotes.size()
        );

        // Consolidación por Producto
        Map<String, List<DesgloseLoteFinancieroDTO>> lotesPorProducto = desgloseLotes.stream()
                .collect(Collectors.groupingBy(DesgloseLoteFinancieroDTO::getProductoNombre));

        List<Producto> productosCalculo = productoId != null
                ? productos.stream().filter(p -> p.getId().equals(productoId)).collect(Collectors.toList())
                : productos;

        List<DesgloseProductoFinancieroDTO> desgloseProductos = productosCalculo.stream().map(prod -> {
            List<DesgloseLoteFinancieroDTO> lotesProd = lotesPorProducto.getOrDefault(prod.getNombre(), List.of());
            BigDecimal costoP = lotesProd.stream()
                    .map(DesgloseLoteFinancieroDTO::getCostoTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal volP = lotesProd.stream()
                    .map(DesgloseLoteFinancieroDTO::getVolumenNeto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            int unidadesP = lotesProd.stream()
                    .mapToInt(DesgloseLoteFinancieroDTO::getUnidadesEstimadas)
                    .sum();

            BigDecimal ingresoP = prod.getPrecio().multiply(BigDecimal.valueOf(unidadesP));
            BigDecimal gananciaP = ingresoP.subtract(costoP);
            BigDecimal margenP = ingresoP.compareTo(BigDecimal.ZERO) > 0
                    ? gananciaP.multiply(BigDecimal.valueOf(100)).divide(ingresoP, 1, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            return new DesgloseProductoFinancieroDTO(
                    prod.getId(),
                    prod.getNombre(),
                    prod.getTipoBebida(),
                    prod.getPrecio().setScale(2, RoundingMode.HALF_UP),
                    lotesProd.size(),
                    costoP.setScale(2, RoundingMode.HALF_UP),
                    volP.setScale(2, RoundingMode.HALF_UP),
                    unidadesP,
                    margenP
            );
        }).collect(Collectors.toList());

        return new DashboardFinancieroDTO(kpis, desgloseLotes, desgloseProductos);
    }
}
