import api from './api';
import type { Course } from '../types';

/**
 * API service layer handling asynchronous network requests to backend endpoints for recommendation.
 */
export const recommendationService = {
  
  getMyRecommendations: (): Promise<Course[]> =>
    api.get<Course[]>('/recommendations/my').then(r => r.data),
};
