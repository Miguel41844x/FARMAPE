package com.farmape.ms.inventario.domain.model;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import static org.assertj.core.api.Assertions.assertThat;

class InventarioMappingTests {

    @Test
    void mapsTablesFromInventarioDatabase() {
        assertThat(Categoria.class.getAnnotation(Table.class).name()).isEqualTo("categorias");
        assertThat(Producto.class.getAnnotation(Table.class).name()).isEqualTo("productos");
        assertThat(LoteProducto.class.getAnnotation(Table.class).name()).isEqualTo("lotes_producto");
        assertThat(MovimientoAlmacen.class.getAnnotation(Table.class).name()).isEqualTo("movimientos_almacen");
    }

    @Test
    void keepsTrabajadorAsExternalReference() throws NoSuchFieldException {
        Column trabajadorColumn = MovimientoAlmacen.class
                .getDeclaredField("idTrabajador")
                .getAnnotation(Column.class);

        assertThat(trabajadorColumn).isNotNull();
        assertThat(trabajadorColumn.name()).isEqualTo("id_trabajador");
        assertThat(MovimientoAlmacen.class.getDeclaredField("idTrabajador").getAnnotation(JoinColumn.class))
                .isNull();
    }

    @Test
    void mapsProductoCategoryRelationship() throws NoSuchFieldException {
        JoinColumn categoriaJoin = Producto.class
                .getDeclaredField("categoria")
                .getAnnotation(JoinColumn.class);

        assertThat(categoriaJoin).isNotNull();
        assertThat(categoriaJoin.name()).isEqualTo("id_categoria");
        assertThat(categoriaJoin.nullable()).isFalse();
    }
}
