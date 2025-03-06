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

    public void add(T entity) {
        em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }
    public void update(T entity) {
        em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }
    public T getById(Class<T> cls, Object id) {
        em = emf.createEntityManager();
        try {
            return em.find(cls, id);
        } finally {
            em.close();
        }
    }
    public List<T> viewAll(Class<T> cls) {
        em = emf.createEntityManager();
        try {
            Query query = em.createQuery("SELECT c FROM "+cls.getSimpleName()+" c", cls); //"FROM " + cls.getSimpleName(), cls);
            List<T> res =  (List<T>) query.getResultList();
            for (T t : res)
                System.out.println(t);
            return res;
        } finally {
            em.close();
        }
    }
    public static void closeFactory() {
        emf.close();
    }
}
