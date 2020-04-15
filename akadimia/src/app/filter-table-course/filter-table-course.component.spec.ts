import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FilterTableCourseComponent } from './filter-table-course.component';

describe('FilterTableCourseComponent', () => {
  let component: FilterTableCourseComponent;
  let fixture: ComponentFixture<FilterTableCourseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FilterTableCourseComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FilterTableCourseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
