import { BasicDataSource } from './basic-data-source';

describe('AbstractDataSource', () => {
  it('should create an instance', () => {
    expect(new BasicDataSource()).toBeTruthy();
  });
});
