package com.jd.survey.domain.settings;

import com.jd.survey.util.SortedSetUpdater;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by ags on 10/5/2017.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "Tags.findAll", query = "select o from Tags o"),
        @NamedQuery(name = "Tags.findById", query = "select o from Tags o where o.id = ?1"),
        @NamedQuery(name = "Tags.findByTagName", query = "select o from Tags o where o.TagName = ?1")

})

public class Tags implements Comparable <Tags>, Serializable	{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    String TagName;

    @NotNull
    @ManyToMany
    @Sort(type = SortType.NATURAL)
    @JoinTable(name="sec_department_tags",joinColumns={@JoinColumn(name="tag_id", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="department_id", referencedColumnName="id")})
    private SortedSet<Department> departments = new TreeSet<Department>();


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


    public String toString() {
        return this.TagName;
    }

    public SortedSet<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(SortedSet<Department> departments) {
        this.departments = departments;
    }

    //Comparable interface
    @Override
    public int compareTo(Tags that) {

        final int BEFORE = -1;
        final int AFTER = 1;
        if (that == null) {
            return BEFORE;
        }
        Comparable<String> thisDepartment = this.getTagName();
        Comparable<String> thatDepartment = that.getTagName();
        if(thisDepartment == null) {
            return AFTER;
        } else if(thatDepartment == null) {
            return BEFORE;
        } else {
            return thisDepartment.compareTo(that.getTagName());
        }

    }
}
