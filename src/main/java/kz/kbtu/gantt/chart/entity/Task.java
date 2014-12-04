package kz.kbtu.gantt.chart.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="tasks")
public class Task implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8847393261273902396L;

	private Long id;
	private Date startDate;
	private Integer duration;
	private String taskDescription;
	private User creator;
	private User responsible;
	private Task parent;
	private List<Task> childs = new ArrayList<Task>();
	private Task sequence;
	private Double progress;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_date")
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	@Column(name = "task_desc")
	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator_id")
	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "responsible_id")
	public User getResponsible() {
		return responsible;
	}

	public void setResponsible(User responsible) {
		this.responsible = responsible;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	public Task getParent() {
		return parent;
	}

	public void setParent(Task parent) {
		this.parent = parent;
	}

	@OneToOne(fetch = FetchType.LAZY, 
			cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	@JoinColumn(name = "sequence_id")
	public Task getSequence() {
		return sequence;
	}

	public void setSequence(Task sequence) {
		this.sequence = sequence;
	}

	@OneToMany(fetch=FetchType.LAZY, mappedBy="parent", orphanRemoval=false,
			cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	public List<Task> getChilds() {
		return childs;
	}

	public void setChilds(List<Task> childs) {
		this.childs = childs;
	}

	public Double getProgress() {
		return progress;
	}

	public void setProgress(Double progress) {
		this.progress = progress;
	}
}
