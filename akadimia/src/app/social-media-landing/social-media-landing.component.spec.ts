import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SocialMediaLandingComponent } from './social-media-landing.component';

describe('SocialMediaLandingComponent', () => {
  let component: SocialMediaLandingComponent;
  let fixture: ComponentFixture<SocialMediaLandingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SocialMediaLandingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SocialMediaLandingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
