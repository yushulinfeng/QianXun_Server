package beans;

import javax.persistence.*;

/**
 * Created by YuShuLinFeng on 2017/4/28.
 */
@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "bl_info", schema = "outapp")
public class InfoEntity {
    private String userid;
    private String noteBook;
    private String todoList;
    private String tomatoClock;
    private String info;

    @Id
    @Column(name = "userid", nullable = false, length = 255)
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Basic
    @Column(name = "note_book", nullable = true, length = -1)
    public String getNoteBook() {
        return noteBook;
    }

    public void setNoteBook(String noteBook) {
        this.noteBook = noteBook;
    }

    @Basic
    @Column(name = "todo_list", nullable = true, length = -1)
    public String getTodoList() {
        return todoList;
    }

    public void setTodoList(String todoList) {
        this.todoList = todoList;
    }

    @Basic
    @Column(name = "tomato_clock", nullable = true, length = -1)
    public String getTomatoClock() {
        return tomatoClock;
    }

    public void setTomatoClock(String tomatoClock) {
        this.tomatoClock = tomatoClock;
    }

    @Basic
    @Column(name = "info", nullable = true, length = -1)
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InfoEntity that = (InfoEntity) o;

        if (userid != null ? !userid.equals(that.userid) : that.userid != null) return false;
        if (noteBook != null ? !noteBook.equals(that.noteBook) : that.noteBook != null) return false;
        if (todoList != null ? !todoList.equals(that.todoList) : that.todoList != null) return false;
        if (tomatoClock != null ? !tomatoClock.equals(that.tomatoClock) : that.tomatoClock != null) return false;
        if (info != null ? !info.equals(that.info) : that.info != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userid != null ? userid.hashCode() : 0;
        result = 31 * result + (noteBook != null ? noteBook.hashCode() : 0);
        result = 31 * result + (todoList != null ? todoList.hashCode() : 0);
        result = 31 * result + (tomatoClock != null ? tomatoClock.hashCode() : 0);
        result = 31 * result + (info != null ? info.hashCode() : 0);
        return result;
    }
}
