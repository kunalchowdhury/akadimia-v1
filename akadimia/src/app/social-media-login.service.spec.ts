import { TestBed } from '@angular/core/testing';

import { SocialMediaLoginService } from './social-media-login.service';

describe('SocialMediaLoginService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: SocialMediaLoginService = TestBed.get(SocialMediaLoginService);
    expect(service).toBeTruthy();
  });
});
