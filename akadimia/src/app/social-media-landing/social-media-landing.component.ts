import {ChangeDetectorRef, Component, Input, OnInit, ViewContainerRef} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {Actions, ofActionCompleted, ofActionSuccessful, Select, Store} from "@ngxs/store";
import {KafkaState} from "../state/kafka.state";
import {Observable} from "rxjs";
import {ConnectWebSocket} from "@ngxs/websocket-plugin";
import {AddMessageAction} from "../state/kafka.actions";
import {MatTableDataSource} from "@angular/material/table";
import {async} from "rxjs-compat/scheduler/async";
import {AuthService} from "../auth.service";



export class Data {
  instructor: string;
  position: number;
  subject: string;
  startdate: string;
  enddate: string;
  videolink: string;
  status : string;


  constructor(instructor: string, position: number, subject: string, startdate: string, enddate: string, videolink: string, status: string) {
    this.instructor = instructor;
    this.position = position;
    this.subject = subject;
    this.startdate = startdate;
    this.enddate = enddate;
    this.videolink = videolink;
    this.status = status;
  }
}


@Component({
  selector: 'app-social-media-landing',
  templateUrl: './social-media-landing.component.html',
  styleUrls: ['./social-media-landing.component.css']
})
export class SocialMediaLandingComponent implements OnInit {
  curUser : any;
  @Input() isAuthenticated: boolean;
  dataList = new MatTableDataSource<Data>();
  displayedColumns: string[] = ['position', 'instructor', 'subject', 'startdate', 'enddate', 'videolink', 'status'];


  @Select(KafkaState.messages)
  kafkaMessages$: Observable<string[]>
  private profileEditorURL: string;
  private curId: string;
  constructor(private route: ActivatedRoute, private router: Router, private store: Store, private actions$: Actions, private authService : AuthService) {
  //  AddMessageAction.type = this.route.snapshot.paramMap.get('id');
    //this.ngOnInit();

    this.dataList = new MatTableDataSource<Data>();

  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataList.filter = filterValue.trim().toLowerCase();
  }

  curval: string[];
  ngOnInit(){
   // this.authService.setAuthentication(false);
    this.dataList = new MatTableDataSource<Data>();
    this.isAuthenticated=false;

  //  alert("I GOT " + this.route.snapshot.paramMap.get('id'));
  //  AddMessageAction.type = this.route.snapshot.paramMap.get('id');
    //https://stackoverflow.com/questions/39407618/angular-2-declaring-an-array-of-objects
    //for (let i = 0; i < 2; i++) {
   // this.dataList.data.push(new Data('Albert E.', 1, 'Physics', '2020-04-10 10:00:00', '2020-04-10 11:00:00', 'www.uou.com'));
    this.store.dispatch(new ConnectWebSocket());
    //alert("I GOT " + this.route.snapshot.paramMap.get('id'));
    this.curId =this.route.snapshot.paramMap.get('id');
    this.actions$.pipe(ofActionCompleted(AddMessageAction)).subscribe((data) => {
      this.kafkaMessages$.subscribe((data) => {
       // alert(data.length);
        this.curval = data;
      });
      this.dataList = new MatTableDataSource<Data>();
      let counter : number = 1;
      this.curval.forEach(s => {
        const strings = s.split(",");
      //  alert(strings[0] +'=='+ this.route.snapshot.paramMap.get('id') );
        if(this.route.snapshot.paramMap.get('id') == strings[0]){
            let url ="";
            if(strings[7] != 'REJECTED') {
              url = strings[6];
              if (!url.startsWith("https:")) {
                url = url.replace("http", "https");
              }
            }
            this.dataList.data.push(new Data(strings[1], counter++, strings[3], strings[4], strings[5],url, strings[7]));
         }
      })
      //this.dataList.data.push(new Data(this.curval, 2, 'Relativity', '2020-04-11 10:00:00', '2020-04-11 11:00:00', 'www.uou1.com'));
      //this.dataList.data.push(new Data('Albert E.', 1, 'Physics', '2020-04-10 10:00:00', '2020-04-10 11:00:00', 'www.uou.com'));
      // this.ngOnInit();


    });

  }

  //ngOnInit() {



  /*getWikiData() {
    this.httpService.getDataFromAPI()
      .subscribe(data => {
        this.parseData(data);
      });
  }

  parseData(jsonData: string) {
    //considering you get your data in json arrays
    for (let i = 0; i < jsonData[1].length; i++) {
      const data = new WikiData(jsonData[1][i], jsonData[2][i]);
      this.wikiData.push(data);
    }
  }*/
  routeProfileEditor() {
    this.router.navigate(['profile-editor', this.curId ], {replaceUrl: true});
  }

  routeFilterTableCourse() {
    this.router.navigate(['filter-table-course', this.curId ], {replaceUrl: true});
  }

  schedSessAsInst() {
    this.router.navigate(['class-schedule-registration', this.curId ], {replaceUrl: true});
  }
}
