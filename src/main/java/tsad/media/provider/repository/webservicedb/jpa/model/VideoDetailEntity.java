package tsad.media.provider.repository.webservicedb.jpa.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;
import java.sql.Time;
import java.util.Date;

@Data
@Entity
@Table(name = "video_detail")
public class VideoDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private BigInteger id;

    @Column(name = "file_name")
    private String filename;

    @Column(name = "extension_name")
    private String extension;

    @Column(name = "duration")
    private Time duration;

    @Column(name = "upload_datetime")
    private Date uploadDatetime;

    @Column(name = "is_available")
    private boolean isAvailable;

    @Column(name = "file_size")
    private long fileSize;

    @Column(name = "resource_path")
    private String resourcePath;
}
