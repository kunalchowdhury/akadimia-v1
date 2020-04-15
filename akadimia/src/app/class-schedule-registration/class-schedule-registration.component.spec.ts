import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ClassScheduleRegistrationComponent } from './class-schedule-registration.component';

describe('ClassScheduleRegistrationComponent', () => {
  let component: ClassScheduleRegistrationComponent;
  let fixture: ComponentFixture<ClassScheduleRegistrationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ClassScheduleRegistrationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClassScheduleRegistrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
