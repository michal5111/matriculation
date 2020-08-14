import {TestBed} from '@angular/core/testing';

import {UsosService} from './usos.service';

describe('UsosService', () => {
  let service: UsosService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UsosService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
