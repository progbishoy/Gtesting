package com.jd.survey.domain.settings;

import com.jd.survey.util.SortedSetUpdater;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Created by ags on 10/5/2017.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "Tags.findAll", query = "select o from Tags o"),
        @NamedQuery(name = "Tags.findById", query = "select o from Tags o where o.id = ?1"),
        @NamedQuery(name = "Tags.findByTagName", query = "select o from Tags o where o.TagName = ?1")

})

public class Tags implements  Serializable	{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    String TagName;


    public String getTagName() {
        return TagName;
    }

    public void setTagName(String tagName) {
        TagName = tagName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
