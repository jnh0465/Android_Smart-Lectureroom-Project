var mongoDB = require('../../dao/student_web/mongoDB');

module.exports = {

    registerProcess : (userInfo, callback)=>{
        const student_id = userInfo.student_id;
        const student_pw = userInfo.student_password;
        const student_name = userInfo.student_name;
        const queryObject = {"student_id":{"$in":[student_id]}}; //몽고디비 쿼리 내용

        mongoDB
        .findStudent(queryObject)
        .then((docsPack)=>{
            const docs = docsPack.docs;
            const amount = docs.length;
            let result = {
                STATE : "SUCCESS",
                DETAIL : "SUCCESS_REGISTER"
            }

            if(amount===1){
                result.STATE = "ERR";
                result.DETAIL = "ALEADY_REGISTERED_ID";
            }

            if(result.STATE ==="SUCCESS"){
                let insertObject={
                    student_id :student_id,
                    student_pw :student_pw,
                    student_name : student_name
                }; //insert내용을 정의해주기

                mongoDB.insertStudent(insertObject)
                .then(()=>{
                    callback(result);
                });
            }
            else{
                callback(result);
            }


        })
    },

    loginProcess : (userInfo, callback)=>{
        const id = userInfo.student_id;
        const pw = userInfo.student_password;

        console.log("-----------------");
        console.log("loginProcess for Android(Student)")
        console.log("id : " + id);
        console.log("pw : " + pw);
        console.log("-----------------");

        if(id==""){ //아이디를 입력하지 않음
            callback({STATE :"ERR" , DETAIL:"EMPTY_ID"});
            return;
        }
        if(pw==""){//비밀번호를 입력하지 않음
            callback({STATE :"ERR" , DETAIL:"EMPTY_PASSWORD"});
            return;
        }

        const queryObject = {"student_id":{"$in":[id]}}; //몽고디비 쿼리 내용
        mongoDB.findStudent(queryObject)
        .then((docsPack)=>{
            const docs = docsPack.docs;
            const amount = docs.length;
            let result={
                STATE : "SUCCESS",
                DETAIL : "SUCCESS_LOGIN",
                STUDENTNAME : docs[0].student_name
            };
            if(amount===1){ //회원가입된 아이디
                const student = docs[0];
                if(student.student_password!==pw){ //비밀번호가 일치하지않음
                    result.STATE="ERR";
                    result.DETAIL="NOT_CORRECT_PASSWORD";
                }
                else{ //아이디와 비밀번호 모두 일치
                    result.data ={
                        id : student.student_id, //로그인 id 저장
                        name : student.student_name //로그인 성명 저장
                    }
                }
            }
            if(amount===0){ //회원가입되지 않은 아이디
                result.STATE="ERR";
                result.DETAIL="NOT_FOUND_ID";
            }


            callback(result);
        })
        .catch((err)=>{console.log(err);});
    },

    changePasswordProcess : (userInfo, callback)=>{
        const id = userInfo.student_id;
        const pw = userInfo.student_password;

        console.log("-----------------");
        console.log("loginProcess for Android(Student)")
        console.log("id : " + id);
        console.log("pw : " + pw);
        console.log("-----------------");

        const queryObject = {"student_id":{"$in":[id]}}; //몽고디비 쿼리 내용
        mongoDB.findStudent(queryObject)
        .then((docsPack)=>{
            const docs = docsPack.docs;
            const amount = docs.length;
            let result={
                STATE : "SUCCESS",
                DETAIL : "SUCCESS_LOGIN"
            };
            if(amount===1){ //회원가입된 아이디
                const student = docs[0];
                if(result.STATE ==="SUCCESS"){
                    let updateObject = {
                         query : {student_id: id},
                         update : { $set: { student_password:pw}}
                    }

                    result.data ={
                        id : student.student_id, //로그인 id 저장
                        name : student.student_name //로그인 성명 저장
                    }

                    mongoDB.updateStudent(updateObject)
                    .then(()=>{
                        callback(result);
                    });
                }
            }
            if(amount===0){ //회원가입되지 않은 아이디
                result.STATE="ERR";
                result.DETAIL="NOT_FOUND_ID";
            }else{
              callback(result);
            }
        })
        .catch((err)=>{console.log(err);});
    },

    getTokenProcess : (userInfo, callback)=>{
        const id = userInfo.student_id;
        const token = userInfo.student_token;

        console.log("-----------------");
        console.log("loginProcess for Android(Student)")
        console.log("id : " + id);
        console.log("토큰 : " + token);
        console.log("-----------------");

        const queryObject = {"student_id":{"$in":[id]}}; //몽고디비 쿼리 내용
        mongoDB.findStudent(queryObject)
        .then((docsPack)=>{
            const docs = docsPack.docs;
            const amount = docs.length;
            let result={
                STATE : "SUCCESS",
                DETAIL : "SUCCESS_LOGIN"
            };
            if(amount===1){ //회원가입된 아이디
                const student = docs[0];
                if(result.STATE ==="SUCCESS"){
                    let updateObject = {
                         query : {student_id: id},
                         update : { $set: { student_token: token}}
                    }

                    result.data ={
                        id : student.student_id, //로그인 id 저장
                        name : student.student_name //로그인 성명 저장
                    }

                    mongoDB.updateStudent(updateObject)
                    .then(()=>{
                        callback(result);
                    });
                }
            }
            if(amount===0){ //회원가입되지 않은 아이디
                result.STATE="ERR";
                result.DETAIL="NOT_FOUND_ID";
            }else{
              callback(result);
            }
        })
        .catch((err)=>{console.log(err);});
    },

    getScheduleProcess : (userInfo, callback)=>{
    const id = userInfo.student_id;
    var info_temp=[];
    var testList = [] ;
    var data = new Object() ;

    const queryObject = {"student_id":{"$in":[id]}}; //몽고디비 쿼리 내용
    mongoDB.getLectureInfo_student(queryObject)
    .then((content)=>{
      const lecture = content.lecture;
      for(let j=0; j<lecture.length; j++){
        for(let k=0; k<lecture[j].lecture_info.length; k++){
            let lecture_id = lecture[j].lecture_id; //강의id
            let lecture_name = lecture[j].lecture_name; //강의명

            let lecture_info = lecture[j].lecture_info[k]; //강의정보
            let day = lecture_info.lecture_time.substr(0,1); // ex) 월2 -> 월  , 2
            let time = lecture_info.lecture_time.substr(1,1);

            let lecture_room = lecture_info.lectureroom; //강의장소
            let building = lecture_room.building_name+" "+lecture_room.lectureroom_num;

            if(day=="월"){
              day=1;
            }else if(day=="화"){
              day=2;
            }else if(day=="수"){
              day=3;
            }else if(day=="목"){
              day=4;
            }else if(day=="금"){
              day=5;
            }
            info_temp.push("a"+day+","+time+","+lecture_name+","+building+","+lecture_id+"a");
          }
        }

        let result={
            STATE : "SUCCESS",
            DETAIL : info_temp

            /*
            //DETAIL : content //쿼리내용 끌고갑니다아아
            {"lecture":
              [
                {"lecture_name":"알고리즘","lecture_id":"1",
                "lecture_info":[
                    {"lectureroom_id":"1","lecture_time":"목8","lectureroom":{"building_name":"실습관","lectureroom_num":"411호","camera_id":"DDIE123"}},
                    {"lectureroom_id":"1","lecture_time":"목9","lectureroom":{"building_name":"실습관","lectureroom_num":"411호","camera_id":"DDIE123"}},
                    {"lectureroom_id":"2","lecture_time":"월2","lectureroom":{"building_name":"실습관","lectureroom_num":"410호","camera_id":"DDIE120"}}
                  ]
                },

                {"lecture_name":"자료구조","lecture_id":"2",
                "lecture_info":[
                  {"lectureroom_id":"2","lecture_time":"화5","lectureroom":{"building_name":"실습관","lectureroom_num":"410호","camera_id":"DDIE120"}},
                  {"lectureroom_id":"2","lecture_time":"화6","lectureroom":{"building_name":"실습관","lectureroom_num":"410호","camera_id":"DDIE120"}},
                  {"lectureroom_id":"2","lecture_time":"화7","lectureroom":{"building_name":"실습관","lectureroom_num":"410호","camera_id":"DDIE120"}}
                ]
              },

                {"lecture_name":"자바스크립트","lecture_id":"3",
                "lecture_info":[
                  {"lectureroom_id":"1","lecture_time":"목1","lectureroom":{"building_name":"실습관","lectureroom_num":"411호","camera_id":"DDIE123"}},
                  {"lectureroom_id":"1","lecture_time":"목2","lectureroom":{"building_name":"실습관","lectureroom_num":"411호","camera_id":"DDIE123"}},
                  {"lectureroom_id":"1","lecture_time":"목3","lectureroom":{"building_name":"실습관","lectureroom_num":"411호","camera_id":"DDIE123"}}
                ]
              }
            ]

            */
        };

        callback(result);
    })
    .catch((err)=>{console.log(err);});
  },

    getAttendStateProcess : (userInfo, callback)=>{
    const id = userInfo.student_id;
    var info_temp=[], testList = [];
    var data = new Object() ;

    const queryObject = {"student_id":{"$in":[id]}}; //몽고디비 쿼리 내용
    mongoDB.getAttendState(queryObject)
    .then((content)=>{
        const attend = content.attend;

        console.log("777777777777777"+JSON.stringify(attend))
        let result={
            STATE : "SUCCESS",
            DETAIL : attend
        };

        callback(result);
    })
    .catch((err)=>{console.log(err);});
}
}
