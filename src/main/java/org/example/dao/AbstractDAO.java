package org.example.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;

public abstract class AbstractDAO<T> {
    private static final String NAME = "JPATest";
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory(NAME);
    protected static EntityManager em;
    private final Class<T> entityClass;

    public AbstractDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.em = emf.createEntityManager();
    }

    public void add(T entity) {
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }
    public void update(T entity) {
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }
    public T getById(Class<T> cls, Object id) {
        return em.find(cls, id);
    }
    public List<T> viewAll(Class<T> cls) {
        Query query = em.createQuery("SELECT c FROM "+cls.getSimpleName()+" c", cls);
        List<T> res =  (List<T>) query.getResultList();
        for (T t : res)
            System.out.println(t);
        return res;
    }
    public void close() {
        if (em.isOpen()) {
            em.close();
        }
    }
    public static void closeFactory() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
