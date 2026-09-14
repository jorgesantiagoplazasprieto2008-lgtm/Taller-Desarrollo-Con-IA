import { memoryStore } from '../../config/db.js';

export async function getDashboardFinanciero(productorId) {
  const productos = memoryStore.productos.filter(p => p.productor_id === productorId);
  const prodIds = productos.map(p => p.id);

  const lotes = memoryStore.lotes.filter(l => prodIds.includes(l.producto_id));

  // Si no hay lotes registrados
  if (lotes.length === 0) {
    return {
      totales: {
        costo_total_acumulado: 0,
        volumen_total_litros: 0,
        merma_total_litros: 0,
        volumen_neto_litros: 0,
        merma_porcentaje_promedio: 0,
        costo_promedio_litro: 0,
        lotes_contabilizados: 0
      },
      desglose_lotes: [],
      desglose_productos: []
    };
  }

  let costoTotalAcumulado = 0;
  let volumenTotalLitros = 0;
  let mermaTotalLitros = 0;

  const desgloseLotes = lotes.map(l => {
    const prod = productos.find(p => p.id === l.producto_id);
    const vol = parseFloat(l.volumen_litros) || 0;
    const merma = parseFloat(l.merma_litros) || 0;
    const costo = parseFloat(l.costo_total) || 0;
    const volNeto = Math.max(0, vol - merma);

    costoTotalAcumulado += costo;
    volumenTotalLitros += vol;
    mermaTotalLitros += merma;

    const mermaPct = vol > 0 ? (merma / vol) * 100 : 0;
    const costoPorLitro = volNeto > 0 ? costo / volNeto : 0;

    // Estimación por presentación (ej. 750ml = 0.75 L, 330ml = 0.33 L)
    let litrosPorUnidad = 0.75;
    if (prod && prod.presentacion.includes('330')) litrosPorUnidad = 0.33;
    if (prod && prod.presentacion.includes('500')) litrosPorUnidad = 0.5;

    const unidadesObtenidas = litrosPorUnidad > 0 ? Math.floor(volNeto / litrosPorUnidad) : 0;
    const costoPorUnidad = unidadesObtenidas > 0 ? costo / unidadesObtenidas : 0;
    const precioVenta = prod ? parseFloat(prod.precio) : 0;
    const ingresoBrutoEstimado = unidadesObtenidas * precioVenta;
    const gananciaBrutaEstimada = ingresoBrutoEstimado - costo;
    const margenBrutoPct = ingresoBrutoEstimado > 0 ? (gananciaBrutaEstimada / ingresoBrutoEstimado) * 100 : 0;

    return {
      lote_id: l.id,
      codigo_trazabilidad: l.codigo_trazabilidad,
      producto_nombre: prod ? prod.nombre : 'Desconocido',
      presentacion: prod ? prod.presentacion : '',
      fecha_produccion: l.fecha_produccion,
      volumen_litros: vol,
      merma_litros: merma,
      merma_porcentaje: Number(mermaPct.toFixed(2)),
      volumen_neto: Number(volNeto.toFixed(2)),
      costo_total: costo,
      costo_por_litro: Number(costoPorLitro.toFixed(2)),
      unidades_estimadas: unidadesObtenidas,
      costo_por_unidad: Number(costoPorUnidad.toFixed(2)),
      precio_venta_unitario: precioVenta,
      margen_bruto_porcentaje: Number(margenBrutoPct.toFixed(1))
    };
  });

  const volumenNetoTotal = Math.max(0, volumenTotalLitros - mermaTotalLitros);
  const mermaPorcentajePromedio = volumenTotalLitros > 0 ? (mermaTotalLitros / volumenTotalLitros) * 100 : 0;
  const costoPromedioLitro = volumenNetoTotal > 0 ? costoTotalAcumulado / volumenNetoTotal : 0;

  // Agrupación por producto
  const desgloseProductos = productos.map(p => {
    const lotesProd = desgloseLotes.filter(l => l.producto_nombre === p.nombre);
    const costoP = lotesProd.reduce((acc, it) => acc + it.costo_total, 0);
    const volP = lotesProd.reduce((acc, it) => acc + it.volumen_neto, 0);
    const unidadesP = lotesProd.reduce((acc, it) => acc + it.unidades_estimadas, 0);
    const ingresoP = unidadesP * p.precio;
    const gananciaP = ingresoP - costoP;
    const margenP = ingresoP > 0 ? (gananciaP / ingresoP) * 100 : 0;

    return {
      producto_id: p.id,
      nombre: p.nombre,
      tipo_bebida: p.tipo_bebida,
      precio: p.precio,
      lotes_count: lotesProd.length,
      costo_acumulado: costoP,
      volumen_neto_total: volP,
      unidades_estimadas_total: unidadesP,
      margen_promedio_porcentaje: Number(margenP.toFixed(1))
    };
  });

  const totalesObj = {
    costo_total_acumulado: Number(costoTotalAcumulado.toFixed(2)),
    volumen_total_litros: Number(volumenTotalLitros.toFixed(2)),
    merma_total_litros: Number(mermaTotalLitros.toFixed(2)),
    volumen_neto_litros: Number(volumenNetoTotal.toFixed(2)),
    porcentaje_merma_global: Number(mermaPorcentajePromedio.toFixed(2)),
    merma_porcentaje_promedio: Number(mermaPorcentajePromedio.toFixed(2)),
    costo_promedio_por_litro: Number(costoPromedioLitro.toFixed(2)),
    costo_promedio_litro: Number(costoPromedioLitro.toFixed(2)),
    lotes_contabilizados: lotes.length
  };

  return {
    kpis: totalesObj,
    totales: totalesObj,
    desglose_por_lote: desgloseLotes,
    desglose_lotes: desgloseLotes,
    desglose_productos: desgloseProductos
  };
}
