// mathches the error response body from GlobalExceptionHandler 
export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}