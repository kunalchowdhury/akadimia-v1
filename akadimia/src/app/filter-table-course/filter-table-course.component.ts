import {
  AfterViewInit, ChangeDetectorRef,
  Component, ComponentFactoryResolver,
  EventEmitter,
  Input,
  NgModule,
  NgZone,
  OnInit,
  Output,
  ViewChild, ViewContainerRef
} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatTableDataSource} from '@angular/material/table';
import {SelectionModel} from "@angular/cdk/collections";
import {ActivatedRoute, Router} from "@angular/router";
import {
  Location,
  Appearance,
  GermanAddress,
  MatGoogleMapsAutocompleteModule
} from '@angular-material-extensions/google-maps-autocomplete';
import {MapsAPILoader} from "@agm/core";
import PlaceResult = google.maps.places.PlaceResult;
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {CourseDialogComponent} from "../course-dialog/course-dialog.component";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {parse, parseISO} from "date-fns";
import {AuthenticationService} from "../shared/authentication.service";
import {ConfirmDialogComponent, ConfirmDialogModel} from "../confirm-dialog/confirm-dialog.component";
import {environment} from "../../environments/environment.prod";


declare let google: any;

interface Subject {
  value: string;
  viewValue: string;
}
export class Sessions {
  name: string;
  subject: string;
  area: string;
  datefrom: string;
  dateto: string;
  url : string;
  sessionid : string
  constructor(name: string, subject: string, area: string, datefrom: string, dateto: string, url: string, sessionid: string) {
    this.name = name;
    this.subject = subject;
    this.area = area;
    this.datefrom = datefrom;
    this.dateto = dateto;
    this.url = url;
    this.sessionid = sessionid;
  }
}



/**
 * @title Table with pagination
 */
@Component({
  selector: 'app-filter-table-course',
  styleUrls: ['filter-table-course.component.css'],
  templateUrl: 'filter-table-course.component.html',

})
export class FilterTableCourseComponent implements OnInit {
  @Input() adressType: string;
  @Output() setAddress: EventEmitter<any> = new EventEmitter();
  @ViewChild('addresstext') addresstext: any;

  autocompleteInput: string;
  queryWait: boolean;
  periodicElement: Sessions;
  public todayDate:any = new Date();
  result: string = '';
  displayedColumns: string[] = ['select', 'subject', 'name', 'area', 'datefrom', 'dateto', 'url', 'sessionid'];
  dataSource = new MatTableDataSource<Sessions>(ELEMENT_DATA);
  selection = new SelectionModel<Sessions>(true, []);
  public zoom: number;
  public latitude: number;
  public longitude: number;
  public selectedAddress: PlaceResult;

  headers = new HttpHeaders({
    'Content-Type': 'application/json'
  });


  subjects: Subject[] = [
    {value: 'biology', viewValue: 'Biology'},
    {value: 'mathematics', viewValue: 'Mathematics'},
    {value: 'storytelling', viewValue: 'Story Telling'},
    {value: 'physics', viewValue: 'Physics'},
    {value: 'hindi', viewValue: 'Hindi'},
    {value: 'english', viewValue: 'English'}
  ];
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild('dynamicComponent', { read: ViewContainerRef }) vc;
  fromDateTime: Date;
  toDateTime: Date;
  selectedSubject: String;
  private mapper: string[];
  selectedSession: any;
  constructor(private route: ActivatedRoute, private router: Router, private mapsAPILoader: MapsAPILoader,
              private ngZone: NgZone, private dialog: MatDialog,
              private http: HttpClient,
              private changeDetectorRefs: ChangeDetectorRef,
              private componentFactoryResolver:ComponentFactoryResolver,
              private authenticationService : AuthenticationService,
              public dialogConfirm: MatDialog) {

  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    this.isAllSelected() ?
      this.selection.clear() :
      this.dataSource.data.forEach(row => this.selection.select(row));
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: Sessions): string {
    if (!row) {
      return `${this.isAllSelected() ? 'select' : 'deselect'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.subject + 1}`;
  }

  ngOnInit() {
    this.dataSource.paginator = this.paginator;

    this.zoom = 10;
    this.latitude = 52.520008;
    this.longitude = 13.404954;

    this.setCurrentPosition();
  }

  private setCurrentPosition() {
    if ('geolocation' in navigator) {
      navigator.geolocation.getCurrentPosition((position) => {
        this.latitude = position.coords.latitude;
        this.longitude = position.coords.longitude;
        this.zoom = 12;
      });
    }
  }

  onAutocompleteSelected(result: PlaceResult) {
    console.log('onAutocompleteSelected: ', result);
  }

  onLocationSelected(location: Location) {
    console.log('onLocationSelected: ', location);
    this.latitude = location.latitude;
    this.longitude = location.longitude;
    //alert(this.latitude+","+this.longitude);
  }

  onGermanAddressMapped($event: GermanAddress) {
    console.log('onGermanAddressMapped', $event);
  }

  onselected($event: Sessions) {
    console.log('onGermanAddressMapped', $event);
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;

   // this.periodicElement = (<HTMLInputElement>event.target).value;
    // @ts-ignore
    this.periodicElement = $event.value ;
    alert($event.sessionid + "selected");
    dialogConfig.data = {
      id: 1,
      description: this.periodicElement.name,
      subject: this.periodicElement.subject

    };

    //const courseDialogComponentComponentFactory = this.componentFactoryResolver.resolveComponentFactory(CourseDialogComponent);

    //const courseDialogComponentComponentRef = this.vc.createComponent(courseDialogComponentComponentFactory);
    //this.vc.clear();
    //courseDialogComponentComponentRef.instance.description='Dynamic Content Loaded';

    //this.dialog.open(courseDialogComponentComponentRef, dialogConfig);

    //const dialogRef = this.dialog.open(CourseDialogComponent, dialogConfig);

    //dialogRef.afterClosed().subscribe(
      //data => console.log("Dialog output:", data)
    //);

    this.authenticationService.SignIn("kunalkumar.chowdhury@gmail.com", "Gopa2slping#*");
  }

  goBack() {
    let myId =this.route.snapshot.paramMap.get('id');
    this.router.navigate(['social-media-landing', myId], {replaceUrl: true});
  }

  searchSessions() {

    // alert(this.fromDateTime.toLocaleString());
    let fromDateLocale = parse(this.fromDateTime.toLocaleString(), 'dd/MM/yyyy, HH:mm:ss', new Date());
    // alert(fromDateLocale.toLocaleString().replace('Z',''));
    // alert(fromDateLocale.toISOString().replace('Z',''));
    let fromDate = parseISO(fromDateLocale.toISOString().replace('Z',''));


    //  alert(this.toDateTime.toLocaleString());
    let toDateLocale = parse(this.toDateTime.toLocaleString(), 'dd/MM/yyyy, HH:mm:ss', new Date());
    //  alert(toDateLocale.toLocaleString().replace('Z',''));
    //  alert(toDateLocale.toISOString().replace('Z',''));
    let toDate = parseISO(toDateLocale.toISOString().replace('Z',''));


    let url = "https://"+environment.hostname+":7003/sessionOnline/searchInstructor?"
    +"subject="+this.selectedSubject
    +"&fromDate="+fromDate.getTime()
    +"&toDate="+toDate.getTime();

    //alert(url);
   // let url = "";
    // alert("submitting form for user >> "+url);
    let sessions : Sessions[] =[];


   this.http.get(url,

      {headers: this.headers, responseType: 'json'}).subscribe(data => {
        //let myId =this.route.snapshot.paramMap.get('id');
        //this.router.navigate(['social-media-landing', myId], {replaceUrl: true});
       //const message = JSON.stringify(data);
       //message.split("");
       let result= <any>data;
       this.mapper= result;
      // alert(this.mapper.length);
       this.dataSource = new MatTableDataSource<Sessions>();
       for (let i = 0; i < this.mapper.length; i++) {
         const message = JSON.stringify(this.mapper[i]);
         //alert("before ");
         if(JSON.parse(message)['coachName'] == null){
          // alert("caught null");
           continue;
         }
         //alert(JSON.parse(message)['coachName']+" , " +
         //  JSON.parse(message)['subject']+" , "+
         //  'Online'+
         //  JSON.parse(message)['startDateTime']+" , "+
         //  JSON.parse(message)['endDateTime']+" ,"+
         //  JSON.parse(message)['videoLink']);
         let thisUrl = JSON.parse(message)['videoLink'];
         if(thisUrl != null && !thisUrl.toString().startsWith("http://")){
             thisUrl = "https://"+thisUrl;
         }
         this.dataSource.data.push(new Sessions(JSON.parse(message)['coachName'],
                                   JSON.parse(message)['subject'],
                             'Online',
                                   JSON.parse(message)['startDateTime'],
           JSON.parse(message)['endDateTime'],
           thisUrl,
           JSON.parse(message)['sessionId']
           ));
       }
       this.changeDetectorRefs.detectChanges();


      },

      error => {
        alert('No matching sessions found');
        console.log('No matching sessions found : ', error);
      });


  }


  onselectedRow(sessionid: any) {
  //  alert("selected is "+ sessionid);

    const message = "Are you sure you want to book this session ?";

    const dialogData = new ConfirmDialogModel("Confirm Action", message);

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: dialogData
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      this.result = dialogResult;
      if(this.result){
        let myId =this.route.snapshot.paramMap.get('id');
        let url = "https://"+environment.hostname+":7003/bookingRequest/?" +
          "&userId="+myId+
          "&sessionId="+sessionid;
        this.http.get(url,

          {headers: this.headers, responseType: 'text'}).subscribe(data => {
            alert("Your request is under processing. Please refresh the page to view status of new requests");
            localStorage.setItem("LAST_LOGIN_TIME", String(new Date().getTime()))
            this.router.navigate(['social-media-landing', myId], {replaceUrl: true});
          },

          error => {
            alert("There was an error . Please retry or book another session.");
            console.log('Log the error here: ', error);
            this.router.navigate(['social-media-landing', myId], {replaceUrl: true});
          });



      }
    });
  }
}

const ELEMENT_DATA: Sessions[] = [
 /* {subject: "Story Telling", name: 'Lyndia Travis', area: "Bilekehalli", datefrom: '27-Mar-2020', dateto: '27-Mar-2020'},
  {subject: "Biology", name: 'Ireena Dutta', area: "Ranka Colony", datefrom: '21-Mar-2020', dateto: '27-Mar-2020'},
  {subject: "Biology", name: 'Mrinal Choudhury', area: "Saukuchi", datefrom: '07-Mar-2020', dateto: '27-Mar-2020'},
  {subject: "Story Telling", name: 'Arnab Dutta', area: "College Street", datefrom: '23-Mar-2020', dateto: '27-Mar-2020'},
  {subject: "Physics", name: 'Kunal Chowdhury', area: "Ranka Colony", datefrom: '12-Mar-2020', dateto: '27-Mar-2020'},
  {subject: "Physics", name: 'Pintu Nayak', area: "Pondicherry", datefrom: '17-Mar-2020', dateto: '27-Mar-2020'},
  {subject: "Chemistry", name: 'Sushanta Chowdhury', area: "Melbourne", datefrom: '22-Mar-2020', dateto: '27-Mar-2020'},
  {subject: "English", name: 'Arnab Hazarika', area: "Gwalior", datefrom: '14-Mar-2020', dateto: '27-Mar-2020'},
  {subject: "English", name: 'Vastav Gogoi', area: "Bhangagarh", datefrom: '10-Mar-2020', dateto: '27-Mar-2020'},
  {subject: "Social Science", name: 'Naresh Sanyal', area: "Indore", datefrom: '08-Mar-2020', dateto: '27-Mar-2020'},
  {subject: "Computers", name: 'Neeraj Kayal', area: "Mumbai", datefrom: '16-Mar-2020', dateto: '27-Mar-2020'},
  {subject: "Mathematics", name: 'Neeraj Kayal', area: "5an Francisco", datefrom: '02-Apr-2020', dateto: '27-Mar-2020'},
  {subject: "Mathematics", name: 'Maitrayee Chowdhury', area: "Rajgarh Road", datefrom: '04-Apr-2020', dateto: '27-Mar-2020'},
  {subject: "Hindi", name: 'Piyush Mishra', area: "Hatibagan", datefrom: '30-Mar-2020', dateto: '27-Mar-2020'},
  {subject: "Hindi", name: 'Subhajit Shyaamal', area: "JP Nagar", datefrom: '25-Mar-2020', dateto: '27-Mar-2020'},
  {subject: "French", name: 'Samantha Fox', area: "New York", datefrom: '19-Mar-2020', dateto: '27-Mar-2020'},
  {subject: "Spanish", name: 'Dora McLaurin', area: "Madrid", datefrom: '15-Mar-2020', dateto: '27-Mar-2020'},
  {subject: "Social Sciences", name: 'Kingshuk Mandal', area: "San Francisco", datefrom: '10-Mar-2020', dateto: '27-Mar-2020'},
  {subject: "Chemisty", name: 'Kajal Deka', area: "Ulubari", datefrom: '19-Mar-2020', dateto: '27-Mar-2020'},
  {subject: "Advanced Mathematics", name: 'Abhijit Borah', area: "Panbazar", datefrom: '10-Apr-2020', dateto: '27-Mar-2020'},*/
];
