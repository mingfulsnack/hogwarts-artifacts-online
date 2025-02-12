package edu.tcu.cs.hogwarts_artifacts_online.hogwartsuser;

import jakarta.transaction.Transactional;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRespository userRespository;


    public UserService(UserRespository userRespository) {
        this.userRespository = userRespository;
    }

    public List<HogwartsUser> findAll() {return this.userRespository.findAll();}

    public HogwartsUser findById(Integer userId) {
        return this.userRespository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
    }

    public HogwartsUser save(HogwartsUser newHogwartsUser) {
        // encode plain password before saving to the database
        return this.userRespository.save(newHogwartsUser);
    }

    public HogwartsUser update(Integer userId, HogwartsUser update) {
        HogwartsUser oldHogwartsUser = this.userRespository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
        oldHogwartsUser.setUsername(update.getUsername());
        oldHogwartsUser.setEnabled(update.isEnabled());
        oldHogwartsUser.setRoles(update.getRoles());
        return this.userRespository.save(oldHogwartsUser);
    }

    /*public void delete(Integer userId) {
        this.userRespository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
        this.userRespository.deleteById(userId);

    }*/
    public void delete(Integer userId) {
        Optional<HogwartsUser> user = this.userRespository.findById(userId);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("user", userId);
        }
        this.userRespository.deleteById(userId);
    }

}
