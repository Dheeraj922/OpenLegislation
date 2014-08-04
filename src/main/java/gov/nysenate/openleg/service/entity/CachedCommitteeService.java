package gov.nysenate.openleg.service.entity;

import gov.nysenate.openleg.dao.entity.CommitteeDao;
import gov.nysenate.openleg.model.entity.*;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class CachedCommitteeService implements CommitteeService{

    private static final Logger logger = LoggerFactory.getLogger(CachedCommitteeService.class);

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private CommitteeDao committeeDao;

    @PostConstruct
    private void init(){
        cacheManager.addCache("committee");
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "committee", key = "#root.methodName + '-' + #committeeId.toString()")
    public Committee getCommittee(CommitteeId committeeId) throws CommitteeNotFoundEx {
        if (committeeId == null) {
            throw new IllegalArgumentException("CommitteeId cannot be null!");
        }
        try{
            return committeeDao.getCommittee(committeeId);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new CommitteeNotFoundEx(committeeId, ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "committee",
               key = "#root.methodName + '-' + #name + '-' + #committeeVersionId.toString()")
    public Committee getCommittee(CommitteeVersionId committeeVersionId) throws CommitteeNotFoundEx {
        if (committeeVersionId == null) {
            throw new IllegalArgumentException("committeeVersionId cannot be null!");
        }
        try {
            return committeeDao.getCommittee(committeeVersionId);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new CommitteeNotFoundEx(committeeVersionId, ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "committee", key = "#root.methodName + '-' + #chamber.toString()")
    public List<Committee> getCommitteeList(Chamber chamber) {
        if (chamber == null) {
            throw new IllegalArgumentException("Chamber cannot be null!");
        }
        return committeeDao.getCommitteeList(chamber);
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "committee", key = "#root.methodName + '-' + #committeeId.toString()")
    public List<Committee> getCommitteeHistory(CommitteeId committeeId) throws CommitteeNotFoundEx {
        if(committeeId==null) {
            throw new IllegalArgumentException("CommitteeId cannot be null!");
        }

        try{
            return committeeDao.getCommitteeHistory(committeeId);
        }
        catch(Exception ex){
            throw new CommitteeNotFoundEx(committeeId, ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = "committee", allEntries = true)
    public void updateCommittee(Committee committee) {
        if(committee==null){
            throw new IllegalArgumentException("Committee cannot be null.");
        }
        committeeDao.updateCommittee(committee);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = "committee", allEntries = true)
    public void deleteCommittee(CommitteeId committeeId) {
        if(committeeId==null) {
            throw new IllegalArgumentException("CommitteeId cannot be null!");
        }
        committeeDao.deleteCommittee(committeeId);
    }
}
