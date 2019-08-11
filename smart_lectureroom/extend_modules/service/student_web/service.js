var mongoDB = require('../../dao/student_web/mongoDB');
var fs = require('fs');

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
                // STUDENTNAME : docs[0].student_name
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

                    result.STUDENTNAME =docs[0].student_name;
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

    buildTimeTable : (userInfo, callback)=>{
        const id = userInfo.id;
        const queryObject = {"student_id":{"$in":[id]}}; //몽고디비 쿼리 내용
        mongoDB.getLectureInfo(queryObject)
        .then((content)=>{
            let result={}; //결과물을 담을 객체
            let timeTableHtml= ""; //시간표 html을 입력할 변수
            const lecture = content.lecture; //쿼리를 통해 얻어온 lecture테이블의 객체
            let dayOfWeek={ //각 요일의 시간표를 생성할 때, 연강을 표현하기 위해서 사용하는 요일별 변수
                mon : 0,
                tue : 0,
                wed : 0,
                thu : 0,
                fri : 0
            }
            let dayOfWeek_eng=["mon","tue","wed","thu","fri"];
            let dayOfWeek_kor=["월","화","수","목","금"];
            /**
             * 가장 바깥쪽의 for문이 한번 실행될때마다 시간표의 1줄씩 생성됨
             *  ex) 1교시, 2교시, ....
             */
            for(let n=0; n<10; n++){ //n+1 값이 교시를 뜻함

                timeTableHtml += "<tr>"
                timeTableHtml +=    "<td class='time'>"+ (n+1) +"교시</td>" //시간표에서  몇 교시인지를 표현

                for(let m=0; m<5; m++){ //m의 값은 요일을 뜻함 0:월, 1:화, 2:수 ......
                    let flag=0; //비어있는 <td> </td>를 입력해야하는지 구분하기 위한 플래그

                    /**
                     * 아래 2개의 for문은 lecture 변수안의 정보를 모두 순회하기 위해 사용
                     * 하나의 정보를 순회할때마다 관련 로직이 실행되고, 적절한 시간표 태그를 생성함
                     */
                    for(let j=0; j<lecture.length; j++){
                        for(let k=0; k<lecture[j].lecture_info.length; k++){
                            let lecture_id = lecture[j].lecture_id; //강의명
                            let lecture_name = lecture[j].lecture_name; //강의명
                            let lecture_info = lecture[j].lecture_info[k]; //강의정보
                            let lecture_room = lecture_info.lectureroom; //강의장소
                            // console.log(lecture_room.camera_id);

                            /**
                             * //lecture_time을 쪼개어 요일을 구함 ex) 월2 -> 월  , 2
                             */
                            let day= lecture_info.lecture_time.substr(0,1);
                            let time = lecture_info.lecture_time.substr(1,1);
                            let key;
                            /**
                             * 위에서 미리 선언한 dayOfWeek_kor, dayOfWeek_eng 배열을 통해
                             * day 안의 한글요일을 영어요일의 인덱스로dayOfWeek_eng[index]
                             * key를 영어 요일로 값을 변경함
                             */
                            for(let z=0; z<5; z++){
                                if(day===dayOfWeek_kor[z]){
                                    day=z;
                                    key=dayOfWeek_eng[z];
                                    break;
                                }
                            }

                            /**
                             * (n+1)==time :
                             * 시간표는 한 행씩 생성됨
                             * 따라서 해당 행에서 의미하는 교시와 lecture의 수업교시가 일치하는지 확인
                             *
                             * (m==day) :
                             * 한 행이 생성되기 전에 작업단위는 하나의 열, 즉 한 칸씩 생성됨(<td></td>)
                             * 하나의 열은 요일을 의미함으로, 현재 생성할 열(m)과 lecture의 요일이 일치하는지 확인
                             *
                             * dayOfWeek[key]==0 :
                             * 시간표에 표현할 과목이 연강이라면 한번에 여러열을 병합하여 표현하게 되는데,
                             * 병합된 칸을 고려하여, <td></td>를 생성하지 말아야 함
                             * dayOfWeek 의 속성들은 이를 위한 값들이며, 이 속성들이 0일때는 표현할 연강이 없음을 의미함
                             */
                            if((n+1)==time && (m==day) && dayOfWeek[key]==0 ){
                                let rowspan=1; //강의의 기본 연강은 1시간

                                day= lecture_info.lecture_time.substr(0,1); //요일
                                time = parseInt(lecture_info.lecture_time.substr(1,1))+1;//교시

                                for(let z =0 ; z< lecture[j].lecture_info.length; z++){
                                    if((day+time)== lecture[j].lecture_info[z].lecture_time){//만약 해당과목이 연강이라면...
                                        rowspan++;
                                        day= lecture[j].lecture_info[z].lecture_time.substr(0,1); //요일
                                        time = parseInt(lecture[j].lecture_info[z].lecture_time.substr(1,1))+1;//교시 , 교시를1 증가함으로써 if문에서 연강인지 확인
                                        z=0; //처음부터 다시 for문을 실행하도록 함

                                    }
                                }
                                /**
                                 * dayOfWeek의 속성값들은 시간표의 한행이 생성되면 -1씩 감소함(최소값은 0)
                                 * 결국 이 속성값들의 의미는 앞으로 몇개의 행을 해당요일에서 <td></td>를 생략할지 알려주는 변수임
                                 * (만약 생략하지않으면 시간표가 중복생성됨)
                                */
                                dayOfWeek[key]=rowspan; //해당요일의 연강이 몇시간인지 값을 할당함

                                //해당 교시, 해당 요일에 <td> </td>를 생성
                                timeTableHtml +=    "<td class='timeTableParts lecture'rowspan='"+rowspan+"'>"
                                timeTableHtml +=        "<div>"
                                timeTableHtml +=            "<div class='lectureName' name='lecture_name'>"+ lecture_name +"</div>"
                                timeTableHtml +=            "<div class='display-none' name='lecture_id'>"+ lecture_id +"</div>"
                                timeTableHtml +=            "<div class='lectureRoom' name='building_name'>"+ lecture_room.building_name +"</div>"
                                timeTableHtml +=            "<div class='lectureRoom' name='lectureroom_num'>"+ lecture_room.lectureroom_num +"</div>"
                                timeTableHtml +=        "</div>"
                                timeTableHtml +=    "</td>"
                                flag++; //비어있는 <td></td>를 생성하지 않도록 플래그 변수를 1증가

                            }
                        }
                    }

                    /**
                     * flag!==1 :
                     * 이미 시간표를 생성하였다면, 비어있는 <td></td>를 생성하지 않도록함
                     *
                     * dayOfWeek[key]==0 :
                     * 표현할 연강이 없으면 , 비어있는 <td></td>를 생성하도록 함
                     *
                     * m :
                     * m은 요일을 뜻함 0:월, 1:화 ......
                     */
                    let key = dayOfWeek_eng[m];
                    if(flag!==1 && dayOfWeek[key]==0){
                        timeTableHtml +=    "<td class='timeTableParts'> </td>"
                    }
                }
                timeTableHtml += "</tr>" ;

                /**
                 * 하나의 행을 모두 생성하였으니
                 * dayOfWeek의 속성들을 1씩 모두 감소(최소값 : 0)
                 * 하나의 행에 대하여, 이 속성값을 통해 <td></td>를 중복생성 방지하기때문
                 * 만약 dayOfWeek[mon]의 값이 3 이라면, 앞으로 3개의 행이 생성될떄까지 <td></td> 생성을 생략함(강의정보가 있는 <td> 포함)
                 */
                for(let key in dayOfWeek){
                    if(dayOfWeek[key]!=0){
                        dayOfWeek[key]--;
                    }
                }

            }

            result.data = timeTableHtml;//시간표 html을 result에 할당
            callback(result);
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
            // console.log(info_temp);

            // var jsonData = JSON.stringify(info_temp) ;
            // console.log(jsonData);

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
        var info_temp=[], idList=[], send_temp=[];
        const queryObject = {"student_id":{"$in":[id]}}; //몽고디비 쿼리 내용

        mongoDB.getAttendState(queryObject)
        .then((content)=>{
            const attend = content.attend;

            for(let i=0; i<attend.length; i++){
              let lecture_id = attend[i].lecture_id;
              let lecture_session = attend[i].lecture_session;
              let attend_state = attend[i].attend_state;
              let lecture_name = attend[i].lecture_name;
              let attend_date = attend[i].attend_date;
              for(let j=0; j<lecture_id.length; j++){
                // info_temp.push("a"+lecture_id[j]+","+lecture_session[j]+","+attend_state[j]+","+lecture_name[j]+","+attend_date[j]+"a");
                info_temp.push({ lecture_id : lecture_id[j], lecture_session : lecture_session[j], attend_state : attend_state[j],lecture_name : lecture_name[j], attend_date: attend_date[j]});
              }
            }
            info_temp.sort(function(a, b) { // 내림차순
                return a.attend_date > b.attend_date ? -1 : a.attend_date < b.attend_date ? 1 : 0;
            });

            for(let i=0; i<info_temp.length; i++){
              let lecture_id = info_temp[i].lecture_id;
              let lecture_session = info_temp[i].lecture_session;
              let attend_state = info_temp[i].attend_state;
              let lecture_name = info_temp[i].lecture_name;
              let attend_date = info_temp[i].attend_date;
              send_temp.push("a"+lecture_id + "," + lecture_session+","+attend_state+","+lecture_name+","+attend_date+"a")
            }

            console.log("안드로이드로 보내는 로그 데이터: "+JSON.stringify(send_temp))

            let result={
                STATE : "SUCCESS",
                DETAIL : send_temp
            };

            callback(result);
        })
        .catch((err)=>{console.log(err);});
    }
}
