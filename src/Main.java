//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import org.hibernate.*;
import Models.Pedido;


public static void main(String[] args) throws HibernateException {
    Pedido pedido = new Pedido();
    pedido.setClienteEmail("SANTICACHONDO@gmail.com");
    pedido.setEstado("DEUDA");
    pedido.setFecha(LocalDate.now());

    Session session = HibernateUtil.getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();
    session.persist(pedido);
    tx.commit();
    System.out.printf("PedidoLinea \n", pedido);
    session.close();
    HibernateUtil.shutdown();


    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        Transaction tx = session.beginTransaction();
        PedidoLinea linea = session.get(PedidoLinea.class, 1L);
        if (linea != null) {
            linea.setCantidad(5);
            session.update(linea);
        }
        tx.commit();
    }

    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        String hql = "SELECT l FROM PedidoLinea l JOIN FETCH l.pedido WHERE l.id = :lineaId";
        PedidoLinea l = session.createQuery(hql, PedidoLinea.class)
                .setParameter("lineaId", 1L)
                .uniqueResult();
        System.out.println("Producto: " + l.getProducto() + " | Email Cliente: " + l.getPedido().getClienteEmail());
    }


    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        String hql = "SELECT l.pedido.id, SUM(l.cantidad * l.precioUnitario) " +
                "FROM PedidoLinea l " +
                "WHERE l.pedido.estado <> :estadoExcluido " +
                "GROUP BY l.pedido.id ORDER BY l.pedido.id";

        List<Object[]> resultados = session.createQuery(hql)
                .setParameter("estadoExcluido", EstadoPedido.CANCELADO)
                .list();

        for (Object[] fila : resultados) {
            System.out.println("Pedido ID: " + fila[0] + " | Total: " + fila[1]);
        }
    }


    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        Transaction tx = session.beginTransaction();
        PedidoLinea lineaAEliminar = session.get(PedidoLinea.class, 4L);
        if (lineaAEliminar != null) {
            session.delete(lineaAEliminar);
        }
        tx.commit();
    }



}
