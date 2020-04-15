import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {environment} from "../environments/environment.prod";
@Injectable({
  providedIn: 'root'
})
export class SocialloginService {
  url;
  constructor(private http: HttpClient) { }
  Savesresponse(response)
  {
    this.url =  'https://'+environment.hostname+':64726/Api/Login/Savesresponse';
    return this.http.post(this.url, response);
  }
}
