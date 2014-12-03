package kz.kbtu.gantt.chart.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="users")
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7648600936250680499L;

	private Long id;
	private String role;
	private String firstName;
	private String secondName;
	private List<Task> createdTasks = new ArrayList<Task>();
	private List<Task> responsibleTasks = new ArrayList<Task>();

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Column(name = "first_name")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "second_name")
	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	@OneToMany(fetch=FetchType.LAZY, mappedBy="creator", orphanRemoval=true,
			cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	public List<Task> getCreatedTasks() {
		return createdTasks;
	}

	public void setCreatedTasks(List<Task> createdTasks) {
		this.createdTasks = createdTasks;
	}

	@OneToMany(fetch=FetchType.LAZY, mappedBy="responsible", orphanRemoval=true,
			cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	public List<Task> getResponsibleTasks() {
		return responsibleTasks;
	}

	public void setResponsibleTasks(List<Task> responsibleTasks) {
		this.responsibleTasks = responsibleTasks;
	}
}
