package com.entrecopas.lote.service;

import com.entrecopas.exception.BusinessException;
import com.entrecopas.exception.ResourceNotFoundException;
import com.entrecopas.exception.UnauthorizedDomainException;
import com.entrecopas.lote.dto.CreateLoteRequest;
import com.entrecopas.lote.dto.LoteDTO;
import com.entrecopas.lote.dto.TrazabilidadPublicaDTO;
import com.entrecopas.lote.model.Lote;
import com.entrecopas.lote.model.ParametrosAnaliticos;
import com.entrecopas.lote.repository.LoteRepository;
import com.entrecopas.producto.model.Producto;
import com.entrecopas.producto.repository.ProductoRepository;
import com.entrecopas.productor.model.Productor;
import com.entrecopas.productor.repository.ProductorRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de negocio para la gestión de lotes, mermas y trazabilidad analítica certificada.
 */
@Service
public class LoteService {

    private final LoteRepository loteRepository;
    private final ProductoRepository productoRepository;
    private final ProductorRepository productorRepository;
    private final CodigoTrazabilidadGenerator codigoGenerator;

    public LoteService(LoteRepository loteRepository,
                       ProductoRepository productoRepository,
                       ProductorRepository productorRepository,
                       CodigoTrazabilidadGenerator codigoGenerator) {
        this.loteRepository = loteRepository;
        this.productoRepository = productoRepository;
        this.productorRepository = productorRepository;
        this.codigoGenerator = codigoGenerator;
    }

    /**
     * Registra un nuevo lote productivo validando pertenencia multi-tenant y consistencia de mermas.
     */
    @Transactional
    public LoteDTO crearLote(Long usuarioId, CreateLoteRequest request) {
        Productor productor = obtenerProductorPorUsuario(usuarioId);

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + request.getProductoId()));

        // Barrera multi-tenant: el producto debe pertenecer al productor autenticado
        if (!producto.getProductor().getId().equals(productor.getId())) {
            throw new UnauthorizedDomainException("No tiene permisos para registrar lotes de un producto ajeno.");
        }

        // Validación estricta de mermas: merma <= volumen
        if (request.getMermaLitros().compareTo(request.getVolumenLitros()) > 0) {
            throw new BusinessException("La merma en litros no puede exceder el volumen total del lote.");
        }

        if (request.getVolumenLitros().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El volumen total debe ser estrictamente positivo.");
        }

        String codigoUnico = codigoGenerator.generarCodigo(productor.getId(), request.getFechaProduccion());

        ParametrosAnaliticos parametros = new ParametrosAnaliticos(
                request.getPh(),
                request.getGraduacionAlcoholica(),
                request.getAcidezTotalGl(),
                request.getDensidad()
        );

        Lote lote = new Lote();
        lote.setProducto(producto);
        lote.setCodigoTrazabilidad(codigoUnico);
        lote.setFechaProduccion(request.getFechaProduccion());
        lote.setVolumenLitros(request.getVolumenLitros());
        lote.setMermaLitros(request.getMermaLitros());
        lote.setCostoTotal(request.getCostoTotal());
        lote.setParametrosAnaliticos(parametros);

        Lote guardado = loteRepository.save(lote);
        return LoteDTO.fromEntity(guardado);
    }

    /**
     * Consulta pública de trazabilidad de un lote por código algorítmico.
     * Enmascara de forma no negociable costos y mermas.
     */
    @Transactional(readOnly = true)
    public TrazabilidadPublicaDTO obtenerTrazabilidadPublica(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new BusinessException("El código de trazabilidad no puede estar vacío.");
        }

        Lote lote = loteRepository.findByCodigoTrazabilidad(codigo.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró ningún lote con el código: " + codigo));

        return TrazabilidadPublicaDTO.fromEntity(lote);
    }

    /**
     * Listado privado de lotes del productor autenticado.
     */
    @Transactional(readOnly = true)
    public List<LoteDTO> listarLotesDelProductor(Long usuarioId) {
        Productor productor = obtenerProductorPorUsuario(usuarioId);
        return loteRepository.findByProductoProductorIdOrderByFechaProduccionDesc(productor.getId())
                .stream()
                .map(LoteDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private Productor obtenerProductorPorUsuario(Long usuarioId) {
        return productorRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de productor no configurado para este usuario."));
    }
}
