import api from './api';
import type { CourseAnalytics } from '../types';

/**
 * API service layer handling asynchronous network requests to backend endpoints for instructor.
 */
export const instructorService = {
  



  async getCourseAnalytics(courseId: string): Promise<CourseAnalytics> {
    const res = await api.get<CourseAnalytics>(`/instructor/courses/${courseId}/analytics`);
    return res.data;
  },
};
