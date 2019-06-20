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
                DETAIL : "SUCCESS_LOGIN"
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
    }
}
