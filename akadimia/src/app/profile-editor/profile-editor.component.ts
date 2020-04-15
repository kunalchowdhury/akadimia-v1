import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {catchError, retry, tap} from "rxjs/operators";
import {Observable, of} from "rxjs";
import {environment} from "../../environments/environment.prod";

interface State {
  name: string;
  cities: string[];
}

@Component({
  selector: 'app-profile-editor',
  templateUrl: './profile-editor.component.html',
  styleUrls: ['./profile-editor.component.css']
})
export class ProfileEditorComponent implements OnInit {
  private coachenabled: boolean;
  constructor(private route: ActivatedRoute, private fb: FormBuilder, private router: Router, private http: HttpClient) {
      this.coachenabled = true;
  }
  ngOnInit() {
    this.coachenabled = true;
  }

  states: State[] = [
    { name: 'Assam', cities: ['Guwahati', 'Dibrugarh'] },
    { name: 'Gujarat', cities: ['Surat', 'Ahmedabad'] },
    { name: 'Karnataka', cities: ['Bangalore', 'Mysore', 'Hubli'] },
    { name: 'Maharashtra', cities: ['Mumbai'] },
    { name: 'West Bengal', cities: ['Asansol','Kolkata'] }

    ];

  httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
  };
  apiUrl = "https://+"+environment.hostname+":7002/userc/";

  cities: string[];
  changeCountry() {
   // alert("got it" + this.selectedValueState);
    this.cities = this.states.find(con => con.name == this.selectedValueState).cities;
  }


  profileForm = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', [Validators.required, Validators.minLength(10)]],
    selfdescription: [''],
    videourl: [''],
    address: this.fb.group({
      street: [''],
      city: [''],
      state: [''],
      zipcode: ['']
    }),
  });
  desc: string;
  urldesc: string;
  selectedValueState: string;
  selectedValueCity: string;

  private handleError<T> (operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
  onSubmit() {
    // TODO: Use EventEmitter with form value
    //console.warn(this.profileForm.value);
    alert(" -- > "+this.profileForm.get("firstName"));
    /*const url = this.apiUrl+"/firstName="+this.profileForm.get("firstName");
    this.http.get<String>(url).pipe(
      tap(_ => console.log(`created new user`)),
      catchError(this.handleError<String>(`An error occured while saving the new user form`))
    );*/
  }

  save() {
   // alert("Thank you . You will receive an email within next 24 hours");
   /* alert(this.profileForm.get("firstName").value);
    alert(this.profileForm.get("lastName").value);
    alert(this.profileForm.get("email").value);
    alert(this.selectedValueState);
    alert(this.selectedValueCity);
    alert(this.profileForm.get('address.zipcode').value);
    alert(this.coachenabled);
    alert(this.profileForm.get("selfdescription").value);*/
    let url = "https://"+environment.hostname+":7002/userc/?"+
      "firstName="+ this.profileForm.get("firstName").value+
      "&lastName="+this.profileForm.get("lastName").value+
      "&emailId="+this.profileForm.get("email").value+
      "&state="+this.selectedValueState+
      "&city="+this.selectedValueCity+
      "&zipCode="+this.profileForm.get("address.zipcode").value+
      "&instructor="+!this.coachenabled+
      "&briefDescription="+this.profileForm.get("selfdescription").value+
      "&youTubeURL="+this.profileForm.get("videourl").value+
      "&phone="+this.profileForm.get("phone").value;


    let url1 = "https://"+environment.hostname+":7002/userc/?firstName=Kunal&lastName=Chowdhury&emailId" +
      "=kunalkumar.chowdhury@gmail.com&state=Karnataka&city=Bangalore&zipCode=780016" +
      "&instructor=true&briefDescription=first%20time%20teacher&youTubeURL=www.youtube.com&phone=9742652223"
    alert("url "+url);
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    const params = new HttpParams();

    const options = {
      headers,
      params,
      withCredentials: true
    };
    let v;
    const val = this.http.get(url1, {headers: headers, responseType: 'text'}).subscribe(data => {
        console.log(data);
        alert("Thank you . You will receive an  email within next 24 hours @"+this.profileForm.get("email").value);

        let myId =this.route.snapshot.paramMap.get('id');
        this.router.navigate(['social-media-landing', myId], {replaceUrl: true});
        //alert("Registration Successful " );//+JSON.stringify(data));
        v = data;
      },

      error => {
       v = error;
        alert("Error Occurred - Are you re-registering with same email id / phone no? "+JSON.stringify(error));
        console.log('Log the error here: ', error);
      });


     alert("*** "+JSON.stringify(val));

  }

  toggle(type: string) {
    this.desc = '';
    this.urldesc = '';
    this.coachenabled = type != 'Coach';

  }

  goBack() {
    let myId =this.route.snapshot.paramMap.get('id');
    this.router.navigate(['social-media-landing', myId], {replaceUrl: true});
  }
}
