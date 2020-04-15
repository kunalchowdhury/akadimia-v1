import { Injectable } from '@angular/core';

import * as firebase from "firebase/app"

import 'firebase/auth';
import 'firebase/firestore';

@Injectable({
  providedIn: 'root'
})

export class AuthenticationService {
  //userData: Observable<firebase.User>;

  constructor() {
    //this.userData = angularFireAuth.authState;
  }

  /* Sign up */
  SignUp(email: string, password: string) {
    firebase
      .auth()
      .createUserWithEmailAndPassword(email, password)
      .then(res => {
        console.log('Successfully signed up!', res);
      })
      .catch(error => {
        console.log('Something is wrong:', error.message);
      });
  }
  firebaseConfig: {
    apiKey: "AIzaSyB-KMTTMUv6sqqJxEku22hB6v_jjyQc_zw",
    authDomain: "whoknows-70227.firebaseapp.com",
    databaseURL: "https://whoknows-70227.firebaseio.com",
    projectId: "whoknows-70227",
    storageBucket: "whoknows-70227.appspot.com",
    messagingSenderId: "514597403527",
    appId: "1:514597403527:web:12746e7faacb14e3a9b122"
  }

  /* Sign in */
  SignIn(email: string, password: string) {
    firebase.auth()
      .signInWithEmailAndPassword(email, password)
      .then(res => {
        alert("Your booking is taken.");
        console.log('Successfully signed in!');
      })
      .catch(err => {
        alert("Something went wrong");
        console.log('Something is wrong:',err.message);
      });
  }

  /* Sign out */
  SignOut() {
    firebase
      .auth()
      .signOut();
  }

}
