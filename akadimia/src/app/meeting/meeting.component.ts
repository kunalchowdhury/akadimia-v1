import { Component, OnInit, ViewChild, ElementRef, NgZone } from '@angular/core';
import { AngularFireDatabase, AngularFireList } from 'angularfire2/database';
import * as firebase from "firebase/app";
import { Observable } from 'rxjs';
import {el} from "@angular/platform-browser/testing/src/browser_util";
import {ActivatedRoute, Router} from "@angular/router";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../environments/environment.prod";
declare let RTCPeerConnection: any;

@Component({
  selector: 'app-meeting',
  templateUrl: './meeting.component.html',
  styleUrls: ['./meeting.component.css']
})
export class MeetingComponent implements OnInit {
  callActive: boolean = false;
  callActive1: boolean = false;
  pc: any;
  pc1: any;
  localStream: any;
  localStream1: any;
  channel: AngularFireList<{}>;
  database: firebase.database.Reference;
  senderId: string;

  @ViewChild("me") me: any;
  @ViewChild("remote") remote: any;
  @ViewChild("remoteScreen") remoteScreen: any;
  @ViewChild("meScreen") meScreen: any;
  private isNegotiating: boolean;
  private isNegotiating1: boolean;
  private channelScreenShare: AngularFireList<{}>;
  private databaseScreenShare: firebase.database.Reference;
  private senderIdScreenShare: string;
  instructor: string;
  student_name: string;
  studentId: string;
  time = new Date();
  timer;
  instructorId: string;
  subjectId: string;
  headers = new HttpHeaders({
    'Content-Type': 'application/json'
  });
    constructor(
    private afDb: AngularFireDatabase, private route: ActivatedRoute,
    private router: Router, private http: HttpClient
  ) { }

  ngOnInit() {

    let myId =this.route.snapshot.paramMap.get('id');
    alert("Loading session  "+myId);
    this.http.get("https://"+environment.hostname+":7004/confirmedReservation/"+myId,

      {headers: this.headers, responseType: 'json'}).subscribe(data => {
        // alert("Your User Id "+JSON.stringify(data));
        const resReq = JSON.stringify(data);
        this.instructor = JSON.parse(resReq)['instructorName'];
        this.subjectId =JSON.parse(resReq)['subject'];
        this.studentId = JSON.parse(resReq)['userId'];
        this.instructorId = JSON.parse(resReq)['instId'];
      },

      error => {
        alert("Sorry, there was a technical problem. Please call @ 974-265-2223");
        console.log('Log the error here: ', error);
      });


/*

    this.instructor = 'Darren Langis';
      this.student_name = 'Siddhartha Lakhera';
      this.studentId = '0001505953';
      this.instructorId = '0002905653';
      this.subjectId = 'Capital Markets';
*/
    this.timer = setInterval(() => {
      this.time = new Date();
    }, 1000);
    this.setupWebRtc();
    this.setupWebRtcScreenShare();
  }

  public ngOnDestroy() {
    this.pc.close();
    this.pc1.close();
    let tracks = this.localStream.getTracks();
    for (let i = 0; i < tracks.length; i++) {
      tracks[i].stop();
    }

    let tracks1 = this.localStream1.getTracks();
    for (let i = 0; i < tracks1.length; i++) {
      tracks1[i].stop();
    }
    this.callActive = false;
    this.callActive1 = false;
  }

  setupWebRtc() {
    this.senderId = this.guid();
    var channelName = "/webrtc";
    this.channel = this.afDb.list(channelName);
    this.database = this.afDb.database.ref(channelName);
    this.database.on("child_added", this.readMessage.bind(this));


    try {
      this.pc = new RTCPeerConnection({
        iceServers: [
          { urls: "stun:stun.services.mozilla.com" },
          { urls: "stun:stun.l.google.com:19302" }
        ]
      }, { optional: [] });

    } catch (error) {
      console.log(error);
      this.pc = new RTCPeerConnection({
        iceServers: [
          { urls: "stun:stun.services.mozilla.com" },
          { urls: "stun:stun.l.google.com:19302" }
        ]
      }, { optional: [] });

    }


    this.pc.onicecandidate = event => {
      event.candidate ? this.sendMessage(this.senderId, JSON.stringify({ ice: event.candidate })) : console.log("Sent All Ice");
    }

    this.pc.onremovestream = event => {
      console.log('Stream Ended');
    }

    this.pc.ontrack = event => {
      (this.remote.nativeElement.srcObject = event.streams[0]); // use ontrack
    }

    this.isNegotiating = false;
    this.pc.onsignalingstatechange = (e) => {  // Workaround for Chrome: skip nested negotiations
      this.isNegotiating = (this.pc.signalingState != "stable");
    }
    this.showMe();

  }

  setupWebRtcScreenShare() {
    this.senderIdScreenShare = this.guidScreenShare();
    var channelName = "/webrtcScreenShare";
    this.channelScreenShare = this.afDb.list(channelName);
    this.databaseScreenShare = this.afDb.database.ref(channelName);
    this.databaseScreenShare.on("child_added", this.readMessage.bind(this));

    try {
      this.pc1 = new RTCPeerConnection({
        iceServers: [
          { urls: "stun:stun.services.mozilla.com" },
          { urls: "stun:stun.l.google.com:19302" }
        ]
      }, { optional: [] });

    } catch (error) {
      console.log(error);
      this.pc1 = new RTCPeerConnection({
        iceServers: [
          { urls: "stun:stun.services.mozilla.com" },
          { urls: "stun:stun.l.google.com:19302" }
        ]
      }, { optional: [] });

    }
    this.pc1.onicecandidate = event => {
      event.candidate ? this.sendMessage(this.senderIdScreenShare, JSON.stringify({ ice: event.candidate })) : console.log("Sent All Ice");
    }

    this.pc1.onremovestream = event => {
      console.log('Stream1 Ended');
    }

    this.pc1.ontrack = event => {
      (this.remoteScreen.nativeElement.srcObject = event.streams[0]); // use ontrack
    }

    this.isNegotiating1 = false;

    this.pc1.onsignalingstatechange = (e) => {  // Workaround for Chrome: skip nested negotiations
      this.isNegotiating1 = (this.pc1.signalingState != "stable");
    }
    this.showMeScreen();
  }

  sendMessage(senderId, data) {
    if(senderId.includes("SC-")) {
      const msg = this.channelScreenShare.push({sender: senderId, message: data});
      msg.remove();
    }else if(!senderId.includes("SC-")) {
      const msg = this.channel.push({sender: senderId, message: data});
      msg.remove();
    }
  }

  async readMessage(data) {
    if (!data) return;
    try {
      var msg = JSON.parse(data.val().message);
      let personalData = data.val().personalData;
      var sender = data.val().sender;
      if ((sender != this.senderId) && (sender != this.senderIdScreenShare))
      {
        //if (this.isNegotiating){return;}
        //this.isNegotiating = true;
        if (msg.ice != undefined ) {
          if(!sender.includes("SC-") && this.pc != null) {
            await this.pc.addIceCandidate(new RTCIceCandidate(msg.ice));
          }else if(sender.includes("SC-") && this.pc1 != null) {
            await this.pc1.addIceCandidate(new RTCIceCandidate(msg.ice));
          }
        } else if (msg.sdp.type == "offer" ) {
          this.callActive = true;
          if(!sender.includes("SC-") && this.pc != null) {
            this.pc.setRemoteDescription(new RTCSessionDescription(msg.sdp))
              .then(() => this.pc.createAnswer())
              .then(answer => this.pc.setLocalDescription(answer))
              .then(() => this.sendMessage(this.senderId, JSON.stringify({ sdp: this.pc.localDescription })));
          }else if(sender.includes("SC-") && this.pc1 != null) {
            this.pc1.setRemoteDescription(new RTCSessionDescription(msg.sdp))
              .then(() => this.pc1.createAnswer())
              .then(answer => this.pc1.setLocalDescription(answer))
              .then(() => this.sendMessage(this.senderIdScreenShare, JSON.stringify({sdp: this.pc1.localDescription})));
          }


        } else if (msg.sdp.type == "answer" ) {
          this.callActive = true;
          if(!sender.includes("SC-") && this.pc != null) {
            await this.pc.setRemoteDescription(new RTCSessionDescription(msg.sdp));
          }else if(sender.includes("SC-") && this.pc1 != null) {
            await this.pc1.setRemoteDescription(new RTCSessionDescription(msg.sdp));
          }

        }
      }
    } catch (error) {
      console.log(error);
    }
  }

  readMessage1(data) {
    if (!data) return;
    try {
      var msg = JSON.parse(data.val().message);
      let personalData = data.val().personalData;
      var sender = data.val().sender;
      if (sender != this.senderId && sender.includes("SC-")) {
        //if (this.isNegotiating){return;}
        //this.isNegotiating = true;
        if (msg.ice != undefined && this.pc1 != null ) {
          this.pc1.addIceCandidate(new RTCIceCandidate(msg.ice));
        } else if (msg.sdp.type == "offer" ) {
          this.callActive1 = true;
          this.pc1.setRemoteDescription(new RTCSessionDescription(msg.sdp))
            .then(() => this.pc1.createAnswer())
            .then(answer => this.pc1.setLocalDescription(answer))
            .then(() => this.sendMessage(this.senderId, JSON.stringify({ sdp: this.pc1.localDescription })));
        } else if (msg.sdp.type == "answer" ) {
          this.callActive1 = true;
          this.pc1.setRemoteDescription(new RTCSessionDescription(msg.sdp));
        }
      }
    } catch (error) {
      console.log(error);
    }
  }

  showMeScreen() {
    // @ts-ignore
    navigator.mediaDevices.getDisplayMedia({ video: true })
      .then(stream => (this.meScreen.nativeElement.srcObject = stream))
      .then(stream => {
        this.pc1.addStream(stream);
        this.localStream1 = stream;

      });
  }


  showMe() {
    navigator.mediaDevices.getUserMedia({ audio: true, video: true })
      .then(stream => (this.me.nativeElement.srcObject = stream))
      .then(stream => {
        this.pc.addStream(stream);
        this.localStream = stream;

      });
  }

  showRemoteScreen() {
    try {
      //if(this.senderId.includes("SC-"))
      {
        this.pc1.createOffer()
          .then(offer => this.pc1.setLocalDescription(offer))
          .then(() => {
            this.sendMessage(this.senderIdScreenShare, JSON.stringify({sdp: this.pc1.localDescription}));
            this.callActive1 = true;
          });
      }
    } catch (error) {
      this.setupWebRtcScreenShare();
      console.log(error);
    }
  }

  showRemote() {
    try {
      //if(!this.senderId.includes("SC-"))
      {
        this.pc.createOffer()
          .then(offer => this.pc.setLocalDescription(offer))
          .then(() => {
            this.sendMessage(this.senderId, JSON.stringify({sdp: this.pc.localDescription}));
            this.callActive = true;
          });
      }
    } catch (error) {
      this.setupWebRtc();
      console.log(error);
    }
  }

  hangup() {
    this.pc.close();
    this.pc1.close();
    let tracks = this.localStream.getTracks();
    for (let i = 0; i < tracks.length; i++) {
      tracks[i].stop();
    }

    let tracks1 = this.localStream1.getTracks();
    for (let i = 0; i < tracks1.length; i++) {
      tracks1[i].stop();
    }
    this.callActive = false;
    this.callActive1 = false;
  }

  guid() {
    return (this.s4() + this.s4() + "-" + this.s4() + "-" + this.s4() + "-" + this.s4() + "-" + this.s4() + this.s4() + this.s4());
  }
  guidScreenShare() {
    return "SC-"+(this.s4() + this.s4() + "-" + this.s4() + "-" + this.s4() + "-" + this.s4() + "-" + this.s4() + this.s4() + this.s4());
  }
  s4() {
    return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
  }

}
