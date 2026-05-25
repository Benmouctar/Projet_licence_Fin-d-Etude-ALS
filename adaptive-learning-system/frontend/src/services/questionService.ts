import api from './api';
import type { Question, QuestionRequest } from '../types';

/**
 * API service layer handling asynchronous network requests to backend endpoints for question.
 */
export const questionService = {
  getQuestions: async (moduleId: string): Promise<Question[]> => {
    const response = await api.get<Question[]>(`/questions/module/${moduleId}`);
    return response.data;
  },

  createQuestion: async (moduleId: string, data: QuestionRequest): Promise<Question> => {
    const response = await api.post<Question>(`/questions/module/${moduleId}`, data);
    return response.data;
  },

  updateQuestion: async (id: string, data: QuestionRequest): Promise<Question> => {
    const response = await api.put<Question>(`/questions/${id}`, data);
    return response.data;
  },

  deleteQuestion: async (id: string): Promise<void> => {
    await api.delete(`/questions/${id}`);
  },
};
