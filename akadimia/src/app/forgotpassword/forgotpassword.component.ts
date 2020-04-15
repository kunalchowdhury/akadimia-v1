import { Component, OnInit } from '@angular/core';
import {FormBuilder, Validators} from "@angular/forms";
import {Router} from "@angular/router";

@Component({
  selector: 'app-forgotpassword',
  templateUrl: './forgotpassword.component.html',
  styleUrls: ['./forgotpassword.component.css']
})
export class ForgotpasswordComponent implements OnInit {

  constructor(private fb: FormBuilder, private router: Router) {
  }

  ngOnInit() {
  }

  profileForm = this.fb.group({
    email: ['', [ Validators.required, Validators.email]],
  });

  submit(){
    this.router.navigate(['social-media-login'], {replaceUrl: true});
  }


  onSubmit() {
    // TODO: Use EventEmitter with form value
    console.warn(this.profileForm.value);
  }

  save() {
    alert("Thank you . You will receive a password reset email in 24 hours");
  }
}
