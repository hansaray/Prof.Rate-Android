const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();

exports.newLikeNotification=functions.database.ref("/ratings/withComment/{rating_id}/likes/{liked_user}").onCreate((data,context)=>{
  const ratingId = context.params.rating_id;
  const likedUserId = context.params.liked_user;

  const r_Uid = admin.database().ref(`/ratings/withComment/${ratingId}/userID`).once('value');
  return r_Uid.then(result=>{
     const rUid = result.val();
     const r_Token = admin.database().ref(`/users/${rUid}/fcm_token`).once('value');
     return r_Token.then(result=>{
       const rToken = result.val();
       var text = "Oylamanıza yeni bir beğeni geldi!"
       var title = "Yeni Beğeni"
       if(likedUserId !== rUid){
         const newLike = {
           notification : {
             click_action : 'android.intent.action.MAIN',
             title : `${title}`,
             body : `${text}`,
             icon : 'default'
           },
           data : {
             likeRatingIDm : `${ratingId}`
           }
         };
         return admin.messaging().sendToDevice(rToken, newLike).then(result=>{
         });
       }
     });
  });
});

exports.newDislikeNotification=functions.database.ref("/ratings/withComment/{rating_id}/dislikes/{liked_user}").onCreate((data,context)=>{
  const ratingId = context.params.rating_id;
  const likedUserId = context.params.liked_user;

  const r_Uid = admin.database().ref(`/ratings/withComment/${ratingId}/userID`).once('value');
  return r_Uid.then(result=>{
     const rUid = result.val();
     const r_Token = admin.database().ref(`/users/${rUid}/fcm_token`).once('value');
     return r_Token.then(result=>{
       const rToken = result.val();
       var text = "Oylamanıza yeni bir beğenmedim geldi!"
       var title = "Yeni Beğenmedim"
       if(likedUserId !== rUid){
         const newLike = {
           notification : {
             click_action : 'android.intent.action.MAIN',
             title : `${title}`,
             body : `${text}`,
             icon : 'default'
           },
           data : {
             dislikeRatingIDm : `${ratingId}`
           }
         };
         return admin.messaging().sendToDevice(rToken, newLike).then(result=>{
         });
       }
     });
  });
});
