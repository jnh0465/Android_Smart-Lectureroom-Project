var baseDBFunction = require('../baseDBFunction'); //mongoDB 관련 커스텀함수 모듈

 module.exports = {
    findStudent : (queryObject)=>{
        const queryPack = baseDBFunction.setQueryPack(queryObject,{});
        return baseDBFunction.queryStudent(queryPack);
    },

    insertStudent : (insertObject)=>{
        return baseDBFunction.insertStudent(insertObject);
    },

    updateStudent : (updateObject)=>{
        return baseDBFunction.updateStudent(updateObject);
    },

    getLectureInfo_student : (queryObject) =>{
    const queryPack = baseDBFunction.setQueryPack(queryObject,{});
    return baseDBFunction.queryStudent(queryPack)
            .then((docsPack)=>{
                return new Promise((resolve, reject)=>{
                    const docs = docsPack.docs;
                    docsPack.queryObject = {"lecture_id":{"$in": docs[0].lecture_list}};
                    resolve(docsPack);
                });
            })
            .then(baseDBFunction.buildQueryPack)
            .then(baseDBFunction.queryLecture)
            .then((docsPack)=>{
                return new Promise((resolve,reject)=>{
                    let arrayForQuery = [];
                    const docs = docsPack.docs;
                    for(let i =0; i<docs.length; i++){
                        for(let k=0; k<docs[i].lecture_info.length; k++){
                            let lectureroom = docs[i].lecture_info[k].lectureroom_id;
                            arrayForQuery.push(lectureroom);
                        }
                    }
                    docsPack.queryObject ={"lectureroom_id":{"$in": arrayForQuery }};
                    resolve(docsPack);
                });
            })
            .then(baseDBFunction.buildQueryPack)
            .then(baseDBFunction.queryLectureRoom)
            .then((docsPack)=>{ //앞서 쿼리한 데이터들을 유용한 형태로 리빌드

                return new Promise((resolve, reject)=>{
                    let content={ //리빌드한 데이터를 담을 객체
                        Student_name : "",
                        lecture : []
                    };

                    const docs = docsPack.docs;
                    const docsObject = docsPack.docsObject;

                    const queryStudentByID = docsObject.queryStudentByID;
                    const queryLectureByID = docsObject.queryLectureByID;
                    const queryLectureRoomByID = docsObject.queryLectureRoomByID;


                    content.Student_name = queryStudentByID[0].Student_name;
                    for(let i=0; i<queryLectureByID.length; i++){
                        let object = {
                            lecture_name: queryLectureByID[i].lecture_name,
                            lecture_id : queryLectureByID[i].lecture_id,
                            lecture_info : queryLectureByID[i].lecture_info
                        }
                        content.lecture.push(object);
                    }

                    for(let i=0; i<content.lecture.length; i++){ //lectureroom_id에 대해서 조인을 구현
                        for(let k=0; k< content.lecture[i].lecture_info.length; k++){
                            for(let j=0; j<queryLectureRoomByID.length; j++){
                                if(content.lecture[i].lecture_info[k].lectureroom_id === queryLectureRoomByID[j].lectureroom_id ){
                                    content.lecture[i].lecture_info[k].lectureroom = {
                                        building_name:queryLectureRoomByID[j].building_name,
                                        lectureroom_num:queryLectureRoomByID[j].lectureroom_num,
                                        camera_id:queryLectureRoomByID[j].camera_id,
                                    }
                                }
                            }
                        }
                    }
                    resolve(content);
                });

            });
          },

    getAttendState : (queryObject) =>{
      const queryPack = baseDBFunction.setQueryPack(queryObject,{});
      let id;
      let content={
          attend : [],
          lecture : [],
          id : []
      };
      let object_attend = {
          lecture_id : [],
          lecture_session : [],
          attend_state : [],
          lecture_name : []
      }

      return baseDBFunction.queryStudent(queryPack)
              .then((docsPack)=>{
                  return new Promise((resolve, reject)=>{
                      const docs = docsPack.docs;
                      id = docs[0].student_id;

                      docsPack.queryObject = {"lecture_id":{"$in": docs[0].lecture_list}};
                      resolve(docsPack);
                  });
              })
              .then(baseDBFunction.buildQueryPack)
              .then(baseDBFunction.queryAttend)
              .then((docsPack)=>{
                  return new Promise((resolve, reject)=>{
                      const docs = docsPack.docs;
                      const docsObject = docsPack.docsObject;
                      const queryAttend = docsObject.queryAttend;

                      let attend = queryAttend;
                      for(let j=0; j<attend.length; j++){
                        let lecture_id = attend[j].lecture_id; //강의 id
                        content.id.push(lecture_id);
                      }

                      docsPack.queryObject = {"lecture_id":{"$in": content.id}};
                      resolve(docsPack);
                  });
                })
                .then(baseDBFunction.queryLecture)
                .then((docsPack)=>{
                    return new Promise((resolve,reject)=>{
                      const docs = docsPack.docs;
                      const docsObject = docsPack.docsObject;

                      const queryStudentByID = docsObject.queryStudentByID;
                      const queryLectureByID = docsObject.queryLectureByID;
                      const queryAttend = docsObject.queryAttend;

                      let attend = queryAttend;
                      for(let j=0; j<attend.length; j++){
                        let lecture_id = attend[j].lecture_id; //강의 id
                        let attend_info = attend[j].attend_info;
                        for(let k=0; k<attend_info.length; k++){
                          let attendence = attend_info[k].attendence;

                          for(let l=0; l<attendence.length; l++){
                            let attend_studentID = attendence[l].student_id;
                            if(id === attend_studentID ){
                              let attend_state = attendence[l].attend_state; //출석상태
                              let lecture_session = attendence[l].lecture_session; //수업차시

                              object_attend.lecture_id.push(lecture_id);
                              object_attend.lecture_session.push(lecture_session);
                              object_attend.attend_state.push(attend_state);
                            }
                          }
                        }
                      }

                      for(let i=0; i<queryLectureByID.length; i++){
                          object_attend.lecture_name.push(queryLectureByID[i].lecture_name);
                      }
                      content.attend.push(object_attend);

                      resolve(content);
                  });
                })
              }
 }
