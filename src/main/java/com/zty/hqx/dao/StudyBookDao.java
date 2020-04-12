package com.zty.hqx.dao;

import com.zty.hqx.model.BookModel;
import com.zty.hqx.model.ExamModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudyBookDao {
    //判断名字是否可用
    @Select("SELECT * FROM book WHERE title = '${title}'")
    @Results({@Result(property = "picUrl", column = "pic"),
            @Result(property = "fileUrl", column = "file")})
    BookModel isTitleAvailable(String title);

//  ---------------------------------------增删数据-----------------------------------------------

    @Insert("INSERT INTO book (title, label, author, synopsis, pic, file, create_time) VALUES ('${title}', #{label.num}, '${author}', '${synopsis}', '${picUrl}', '${fileUrl}', now())")
    void insertBook(BookModel model);

    @Delete("DELETE FROM book WHERE id = #{id}")
    void deletebook(int id);

//  ---------------------------------------更新数据-----------------------------------------------

    @Update("UPDATE book SET title = '${title}', label = #{label.num}, author = '${author}', synopsis = '${synopsis}',pic = '${picUrl}',file = '${fileUrl}' WHERE id = #{id}")
    void updateBook(BookModel model);

    @Update("update book set count=count+1 where id = #{id}")
    void updateBookCount(int id);

//  ------------------------------------------------获取数据-----------------------------------------------

    @Select("SELECT * FROM book ORDER BY book.count DESC LIMIT #{limit}")
    @Results({@Result(property = "picUrl", column = "pic"),
            @Result(property = "fileUrl", column = "file")})
    List<BookModel> getBookByCount(int limit);

    @Select("SELECT * FROM book ORDER BY create_time DESC LIMIT #{limit}")
    @Results({@Result(property = "picUrl", column = "pic"),
            @Result(property = "fileUrl", column = "file")})
    List<BookModel> getBookByTime(int limit);

    @Select("SELECT * FROM book WHERE label = #{label} and book.id > #{num} ORDER BY id LIMIT #{limit}")
    @Results({@Result(property = "picUrl", column = "pic"),
            @Result(property = "fileUrl", column = "file")})
    List<BookModel> getBookByLabel(int num, int limit, int label);

    @Select("SELECT * FROM book WHERE title LIKE '%${key}%' and id > #{num} ORDER BY id LIMIT #{limit}")
    @Results({@Result(property = "picUrl", column = "pic"),
            @Result(property = "fileUrl", column = "file")})
    List<BookModel> getBookByTitle(String key, int num, int limit);

    @Select("SELECT id, label, count FROM book")
    List<BookModel> getAllBookCount();

    @Select("SELECT * FROM book WHERE id > #{num} ORDER BY id LIMIT #{limit}")
    @Results({@Result(property = "picUrl", column = "pic"),
            @Result(property = "fileUrl", column = "file")})
    List<BookModel> getBookById(int num, int limit);

    @Select("SELECT * FROM book, e_book WHERE book.id = #{id} AND e_book.num = book.label;")
    @Results({@Result(property = "picUrl", column = "pic"),
            @Result(property = "fileUrl", column = "file"),
            @Result(property = "label.num", column = "num"),
            @Result(property = "label.msg", column = "msg"),
            @Result(property = "label.english", column = "english"),})
    BookModel getBook(int id);

    @Select("select count(1) from book")
    int getBookCount();
}
