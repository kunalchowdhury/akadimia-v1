import {Component, Inject, Input, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
//import * as firebase from 'firebase';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import * as firebase from "firebase";
import {AuthenticationService} from "../shared/authentication.service";


@Component({
  selector: 'app-course-dialog',
  templateUrl: './course-dialog.component.html',
  styleUrls: ['./course-dialog.component.css']
})
export class CourseDialogComponent implements OnInit {

  description:string;
  subject:string;
  someValue: boolean = false;
  public recaptchaVerifier: firebase.auth.PhoneAuthProvider;
  private sent: boolean;
  email: string;
  address: FormControl;
  phone_number: number;
  busy: boolean;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<CourseDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public authenticationService: AuthenticationService) {

    this.description = data.description;
    this.subject = data.subject;
    //this.email = new FormControl('', [Validators.required, Validators.email]);
    this.address = new FormControl('', [Validators.required]);
    /* REPLACE CONFIG DETAILS BELOW WITH YOURS */
    const firebaseConfig = {
      apiKey: "AIzaSyDSCVaB1Yj968Qle6MjNzusjCZJh3wzEQ0",
      authDomain: "tachyon-229915.firebaseapp.com",
      databaseURL: "https://tachyon-229915.firebaseio.com",
      projectId: "tachyon-229915",
      storageBucket: "tachyon-229915.appspot.com",
      messagingSenderId: "108135056161",
      appId: "1:108135056161:web:8d57ff823280f236083460"
    };
    //if (!firebase.apps.length) {
      //firebase.initializeApp(firebaseConfig);
    //}
  }
  onNoClick(): void {
    this.dialogRef.close(this.someValue);
  }
  ngOnInit() {
    /*this.form = this.fb.group({
      description: [this.description, []],

    });
*/
    this.recaptchaVerifier = new firebase.auth.PhoneAuthProvider();

  }


  password: string;



  signUp() {
    this.authenticationService.SignUp(this.email, this.password);
    this.email = '';
    this.password = '';
  }

  signIn() {
    this.email = '';
    this.password = '';
  }

  signOut() {
    this.authenticationService.SignOut();
  }

  onSubmit(formData) {
   /* const appVerifier = this.recaptchaVerifier;
    const phoneNumberString = this.phone_number.toString();
    alert("p "+phoneNumberString);
    firebase.auth().signInWithPhoneNumber(phoneNumberString, appVerifier)
      .then((confirmationResult) => {
        this.sent = true;
        const verification = prompt('Enter verification code');
        if (verification != null) {
          console.log(verification);
          confirmationResult.confirm(verification)
            .then((good) => {
              // all checks out
              alert("All Set To Go");
            })
            .catch((bad) => {
              alert("Verification Failed");
            });
        } else {
          console.log('No verification code entered');
        }
      })
      .catch((err) => {
        alert("sms not sent");
        console.log('sms not sent', err);
      });*/
  };

  save() {
    this.dialogRef.close();
   // alert("Thank you . You will receive an email at "+this.email.value+" within next 24 hours");
  }

  close() {
    this.dialogRef.close();
  }

}
