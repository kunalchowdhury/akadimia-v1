import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CorporateLoginComponent } from './corporate-login.component';

describe('CorporateLoginComponent', () => {
  let component: CorporateLoginComponent;
  let fixture: ComponentFixture<CorporateLoginComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CorporateLoginComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CorporateLoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
