package ru.feverhowl.petprojecttest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.feverhowl.petprojecttest.DefaultRecord;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Dmitrii Zolotarev
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "pet")
public class Pet extends DefaultRecord {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "species")
    private String species;

    @Column(name = "name")
    private String name;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="CET") // Asia/Tbilisi
    @Column(name = "birth_day")
    private Date birthDay;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS", timezone="CET") // Asia/Tbilisi
    @Column(name = "createdAt")
    private Timestamp createdAt;

    @Override
    public String getTableName() {
        return this.getClass().getAnnotation(Table.class).name();
    }
}
