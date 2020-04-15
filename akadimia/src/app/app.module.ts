import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatGoogleMapsAutocompleteModule } from '@angular-material-extensions/google-maps-autocomplete';
import {AgmCoreModule} from "@agm/core";
import { MatToolbarModule,
  MatMenuModule,
  MatIconModule,
  MatButtonModule,
  MatTableModule,
  MatDividerModule,
  MatInputModule,
  MatCardModule,
  MatSlideToggleModule,
  MatSelectModule,
  MatOptionModule} from '@angular/material';

import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { MatPaginatorModule } from '@angular/material';
/* Auth service */
import { AuthenticationService } from './shared/authentication.service';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { GameComponent } from './game/game.component';
import { LoginComponent } from './login/login.component';
import { SpeedDialFabComponent } from './speed-dial-fab/speed-dial-fab.component';
import { CorporateLoginComponent } from './corporate-login/corporate-login.component';
import { SocialMediaLoginComponent } from './social-media-login/social-media-login.component';
import { SocialMediaDashboardComponent } from './social-media-dashboard/social-media-dashboard.component';

import { HttpClientModule } from '@angular/common/http';
import {GoogleLoginProvider, FacebookLoginProvider, AuthService, LinkedinLoginProvider} from 'angular-6-social-login';
import { SocialLoginModule, AuthServiceConfig } from 'angular-6-social-login';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import { FilterTableCourseComponent } from './filter-table-course/filter-table-course.component';
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatNativeDateModule} from "@angular/material/core";
import {MatRadioModule} from "@angular/material/radio";
import {MatSortModule} from "@angular/material/sort";
import { CourseDialogComponent } from './course-dialog/course-dialog.component';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import { ProfileEditorComponent } from './profile-editor/profile-editor.component';
import {AngularFireDatabaseModule} from "angularfire2/database";
import {AngularFireAuthModule} from "angularfire2/auth";
import {AngularFireModule} from "angularfire2";
import {FirebaseApp} from "angularfire2";


export function socialConfigs() {
  const config = new AuthServiceConfig(
    [
      {
        id: FacebookLoginProvider.PROVIDER_ID,
        provider: new FacebookLoginProvider('app -id')
      },
      {
        id: GoogleLoginProvider.PROVIDER_ID,
        provider: new GoogleLoginProvider('app-id')
      },
      {
        id: LinkedinLoginProvider.PROVIDER_ID,
        provider: new LinkedinLoginProvider("1098828800522-m2ig6bieilc3tpqvmlcpdvrpvn86q4ks.apps.googleusercontent.com")
      }
    ]
  );
  return config;
}


import { MeetingComponent } from './meeting/meeting.component';
import { ForgotpasswordComponent } from './forgotpassword/forgotpassword.component';
import { SocialMediaLandingComponent } from './social-media-landing/social-media-landing.component';
import {NgxsModule} from "@ngxs/store";
import {KafkaState} from "./state/kafka.state";
import {NgxsWebsocketPluginModule} from "@ngxs/websocket-plugin";
import { ClassScheduleRegistrationComponent } from './class-schedule-registration/class-schedule-registration.component';
import {OWL_DATE_TIME_FORMATS, OwlDateTimeModule, OwlNativeDateTimeModule} from "ng-pick-datetime";
import {environment} from "../environments/environment.prod";
import { ConfirmDialogComponent } from './confirm-dialog/confirm-dialog.component';


const afirebaseApp = AngularFireModule.initializeApp(environment.firebaseConfig);


// @ts-ignore
@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    GameComponent,
    LoginComponent,
    SpeedDialFabComponent,
    CorporateLoginComponent,
    SocialMediaLoginComponent,
    SocialMediaDashboardComponent,
    FilterTableCourseComponent,
    CourseDialogComponent,
    ProfileEditorComponent,
    MeetingComponent,
    ForgotpasswordComponent,
    SocialMediaLandingComponent,
    ClassScheduleRegistrationComponent,
    ConfirmDialogComponent


  ],
  imports: [
    HttpClientModule,
    OwlDateTimeModule,
    OwlNativeDateTimeModule,
    AngularFireModule.initializeApp({
      apiKey: "AIzaSyB-KMTTMUv6sqqJxEku22hB6v_jjyQc_zw",
      authDomain: "whoknows-70227.firebaseapp.com",
      databaseURL: "https://whoknows-70227.firebaseio.com",
      projectId: "whoknows-70227",
      storageBucket: "whoknows-70227.appspot.com",
      messagingSenderId: "514597403527",
    }),
    // important !!!
    AgmCoreModule.forRoot({
      apiKey: 'AIzaSyCVXBurmXsKtPdXbceBIx3oKqc2uiw3rXc',
      libraries: ['places']
    }),
    MatGoogleMapsAutocompleteModule,
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    FlexLayoutModule,
    FormsModule,
    ReactiveFormsModule,
    MatToolbarModule,
    MatInputModule,
    MatCardModule,
    MatMenuModule,
    MatIconModule,
    MatButtonModule,
    MatTableModule,
    MatDividerModule,
    MatSlideToggleModule,
    MatSelectModule,
    MatOptionModule,
    HttpClientModule,
    SocialLoginModule,
    MatPaginatorModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatRadioModule,
    MatSortModule,
    MatDialogModule,
    AngularFireDatabaseModule,
    AngularFireAuthModule,
    NgxsModule.forRoot([
      KafkaState
    ]),
    NgxsWebsocketPluginModule.forRoot({
      url: 'ws://127.0.0.1:8080/websocket'
    }),
    OwlDateTimeModule


  ],
  providers: [AuthService,
    { provide: MAT_DIALOG_DATA, useValue: {} },
    {
      provide: MatDialogRef,
      useValue: {}
    },
    {
      provide: AuthServiceConfig,
      useFactory: socialConfigs
    },
    AuthenticationService
    ],



  entryComponents: [CourseDialogComponent, FilterTableCourseComponent, ConfirmDialogComponent],
  bootstrap: [AppComponent]
})

export class AppModule { }
