package za.ac.cput.marginhotelmanagement.service;

import java.util.List;

public interface IService<T, ID> {
    T create(T t);
    T read(ID id);
    T update(T t);
    boolean delete(T t);
    List<T> findAll();

    /**
     * Convenience default method to delete an entity by its id.
     */
    default boolean deleteById(ID id) {
        T entity = read(id);
        if (entity == null) return false;
        return delete(entity);
    }
}
