import { TestBed } from '@angular/core/testing';

import { SimpleAuthServiceService } from './simple-auth-service.service';

describe('SimpleAuthServiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: SimpleAuthServiceService = TestBed.get(SimpleAuthServiceService);
    expect(service).toBeTruthy();
  });
});
