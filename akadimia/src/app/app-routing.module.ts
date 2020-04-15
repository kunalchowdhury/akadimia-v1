import { LoginComponent } from './login/login.component';
import { AuthGuardService } from './auth-guard.service';
import { HomeComponent } from './home/home.component';
import { GameComponent } from './game/game.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {CorporateLoginComponent} from './corporate-login/corporate-login.component';
import {SocialMediaLoginComponent} from './social-media-login/social-media-login.component';
import {SocialMediaDashboardComponent} from './social-media-dashboard/social-media-dashboard.component';
import {FilterTableCourseComponent} from "./filter-table-course/filter-table-course.component";
import {ProfileEditorComponent} from "./profile-editor/profile-editor.component";
import {MeetingComponent} from "./meeting/meeting.component";
import {ForgotpasswordComponent} from "./forgotpassword/forgotpassword.component";
import {SocialMediaLandingComponent} from "./social-media-landing/social-media-landing.component";
import {ClassScheduleRegistrationComponent} from "./class-schedule-registration/class-schedule-registration.component";
import {CourseDialogComponent} from "./course-dialog/course-dialog.component";

const routes: Routes = [
  {
    path: 'home',
    component: HomeComponent
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'corporate-login',
    component: CorporateLoginComponent
  },
  {
    path: 'game',
    component: GameComponent,
    canActivate: [ AuthGuardService ]
  },
  {
    path: 'social-media-login',
    component: SocialMediaLoginComponent,
    data: {
      title: 'Login Page'
    }
  },
  {
    path: 'social-media-dashboard',
    component: SocialMediaDashboardComponent,
    data: {
      title: 'Dashboard Page'
    },

  },
  {
    path: 'filter-table-course',
    component: FilterTableCourseComponent,
    canActivate: [ AuthGuardService ]
  },
  {
    path: 'filter-table-course/:id',
    component: FilterTableCourseComponent,
    canActivate: [ AuthGuardService ]
  },
  {
    path: 'profile-editor',
    component: ProfileEditorComponent,
    canActivate: [ AuthGuardService ]
  },
  {
    path: 'profile-editor/:id',
    component: ProfileEditorComponent,
    canActivate: [ AuthGuardService ]
  },
  {
    path: 'meeting',
    component: MeetingComponent


  },
  {
    path: 'meeting/:id',
    component: MeetingComponent


  },
  {

    path: 'forgot-password',
    component: ForgotpasswordComponent
  },
  {
    path: 'social-media-landing',
    component: SocialMediaLandingComponent,
    canActivate: [ AuthGuardService ]

  },
  {
    path: 'social-media-landing/:id',
    component: SocialMediaLandingComponent,
    canActivate: [ AuthGuardService ]

  },
  {
    path: 'class-schedule-registration',
    component: ClassScheduleRegistrationComponent,
    canActivate: [ AuthGuardService ]

  },
  {
    path: 'class-schedule-registration/:id',
    component: ClassScheduleRegistrationComponent,
    canActivate: [ AuthGuardService ]

  },
  {
    path: 'course-dialog-component',
    component: CourseDialogComponent
  }





];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
