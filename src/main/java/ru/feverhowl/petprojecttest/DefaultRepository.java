package ru.feverhowl.petprojecttest;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Dmitrii Zolotarev
 */
@NoRepositoryBean
public interface DefaultRepository<D extends DefaultRecord> extends CrudRepository<D, String> {
    @Transactional void deleteAllById(String id);
}
