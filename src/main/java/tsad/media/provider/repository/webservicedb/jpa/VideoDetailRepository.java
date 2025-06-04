package tsad.media.provider.repository.webservicedb.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tsad.media.provider.repository.webservicedb.jpa.model.VideoDetailEntity;

@Repository
public interface VideoDetailRepository extends JpaRepository<VideoDetailEntity, String> {
}
