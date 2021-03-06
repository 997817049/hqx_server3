package com.zty.hqx.dao;

import com.zty.hqx.model.ExamContentModel;
import com.zty.hqx.model.ExamModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudyExamDao {

    //判断名字是否可用
    @Select("SELECT * FROM exam WHERE title = '${title}'")
    ExamModel isTitleAvailable(String title);

//  ---------------------------------------增数据-----------------------------------------------

    @Insert("INSERT INTO exam_content (id, num, type, question, optionA, optionB, optionC, optionD, answer, analysis, create_time) " +
            "VALUES (#{examId}, #{model.num}, #{model.type.num}, '${model.question}', '${model.optionA}', '${model.optionB}' , '${model.optionC}',  '${model.optionD}',  '${model.answer}', '${model.analysis}', now())")
    void insertExamContent(int examId, ExamContentModel model);

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO exam (title, label, time, create_time) VALUES ('${title}', #{label.num}, #{time}, now())")
    void insertExam(ExamModel model);

//  ---------------------------------------删数据-----------------------------------------------

    @Delete("DELETE FROM exam_content WHERE id = #{id}")
    void deleteExamAllContent(int id);

    @Delete("DELETE FROM exam_content WHERE id = #{id} and num = #{num}")
    void deleteExamContent(int id, int num);

    @Delete("DELETE FROM exam WHERE id = #{id}")
    void deleteExam(int id);

//    ------------------------------------更新数据---------------------------------------------------

    @Update("UPDATE exam SET title = '${title}', label = #{label.num}, time = #{time} WHERE id = #{id}")
    void updateExam(ExamModel model);

    //更新count
    @Update("update exam set count=count+1 where id = #{id}")
    void updateExamCount(int id);

    @Update("UPDATE exam_content SET type = #{model.type.num}, question = '${model.question}', optionA = '${model.optionA}',optionB = '${model.optionB}',optionC = '${model.optionC}',optionD = '${model.optionD}', answer = '${model.answer}', analysis = '${model.analysis}' WHERE id = #{id} and exam_content.num = #{model.num}")
    void updateExamContent(int id, ExamContentModel model);

//---------------------------------------获取数据---------------------------------------------------

    @Select("SELECT * FROM exam, e_exam where e_exam.num = exam.label ORDER BY count DESC LIMIT #{limit};")
    @Results({@Result(property = "label.num", column = "num"),
            @Result(property = "label.msg", column = "msg"),
            @Result(property = "label.english", column = "english"),})
    List<ExamModel> getExamByCount(int limit);

    @Select("SELECT * FROM exam, e_exam where e_exam.num = exam.label ORDER BY exam.create_time DESC LIMIT #{limit};")
    @Results({@Result(property = "label.num", column = "num"),
            @Result(property = "label.msg", column = "msg"),
            @Result(property = "label.english", column = "english"),})
    List<ExamModel> getExamByTime(int limit);

    @Select("SELECT * FROM exam, e_exam WHERE e_exam.num = exam.label and id > #{num} and label = #{label} " +
            "ORDER BY id LIMIT #{limit};")
    @Results({@Result(property = "label.num", column = "num"),
            @Result(property = "label.msg", column = "msg"),
            @Result(property = "label.english", column = "english"),})
    List<ExamModel> getExamByLabel(int num, int limit, int label);

    @Select("SELECT * FROM exam, e_exam WHERE e_exam.num = exam.label and title LIKE '%${key}%' and id > #{num} ORDER BY id LIMIT #{limit}")
    @Results({@Result(property = "label.num", column = "num"),
            @Result(property = "label.msg", column = "msg"),
            @Result(property = "label.english", column = "english"),})
    List<ExamModel> getExamByTitle(String key, int num, int limit);

    @Select("SELECT * FROM exam, e_exam WHERE e_exam.num = exam.label ORDER BY id LIMIT #{limit} OFFSET #{num}")
    @Results({@Result(property = "label.num", column = "num"),
            @Result(property = "label.msg", column = "msg"),
            @Result(property = "label.english", column = "english"),})
    List<ExamModel> getExamByNum(int num, int limit);

    @Select("SELECT id, label, count, e_exam.* FROM exam, e_exam WHERE e_exam.num = exam.label")
    @Results({@Result(property = "label.num", column = "num"),
            @Result(property = "label.msg", column = "msg"),
            @Result(property = "label.english", column = "english"),})
    List<ExamModel> getAllExamCount();

    @Select("SELECT * FROM exam, e_exam WHERE e_exam.num = exam.label and id = #{id}")
    @Results({@Result(property = "label.num", column = "num"),
            @Result(property = "label.msg", column = "msg"),
            @Result(property = "label.english", column = "english"),})
    ExamModel getExamById(int id);

    @Select("SELECT id, exam_content.num, type, question, optionA, optionB, optionC, optionD, answer, analysis, e_exam_content.num as num1, msg" +
            " FROM exam_content, e_exam_content WHERE id = #{id} and e_exam_content.num = exam_content.type")
    @Results({
            @Result(property = "type.num", column = "num1"),
            @Result(property = "type.msg", column = "msg")
    })
    List<ExamContentModel> getExamContent(int id);

    @Select("SELECT id, exam_content.num, type, question, optionA, optionB, optionC, optionD, answer, analysis, e_exam_content.num as num1, msg" +
            " FROM exam_content, e_exam_content WHERE id = #{id} and exam_content.num = #{num} and e_exam_content.num = exam_content.type")
    @Results({
            @Result(property = "type.num", column = "num1"),
            @Result(property = "type.msg", column = "msg")
    })
    ExamContentModel getQuestion(int id, int num);

    @Select("select count(1) from exam")
    int getExamCount();
}
