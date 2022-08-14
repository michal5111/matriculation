export class BackendError {
  error: string;
  message: string;
  path: string;
  status: number;
  timestamp: string;
  trace: string;

  constructor(error: string, message: string, path: string, status: number, timestamp: string, trace: string) {
    this.error = error;
    this.message = message;
    this.path = path;
    this.status = status;
    this.timestamp = timestamp;
    this.trace = trace;
  }

  static [Symbol.hasInstance](obj: { error: any; message: any; path: any; status: any; timestamp: any; trace: any; }) {
    if (obj.error && obj.message && obj.path && obj.status && obj.timestamp && obj.trace) {
      return true;
    }
  }
}
