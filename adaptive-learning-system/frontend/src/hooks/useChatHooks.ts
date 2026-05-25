import { useQuery, useMutation } from '@tanstack/react-query';
import { chatService } from '../services/chatService';
import type { ChatResponse } from '../types';





/**
 * Custom React hook for encapsulating and exposing state, operations, and queries related to Chat Hooks.
 */
export const useChatHistory = (moduleId: string, enrollmentId: string) => {
  return useQuery({
    queryKey: ['chatHistory', moduleId, enrollmentId],
    queryFn: () => chatService.getHistory(moduleId, enrollmentId),
    enabled: Boolean(moduleId) && Boolean(enrollmentId),
    staleTime: 1000 * 60 * 5, 
    retry: false,
  });
};

interface SendMessageVars {
  moduleId: string;
  enrollmentId: string;
  query: string;
}




export const useSendMessage = () => {
  return useMutation<ChatResponse, Error, SendMessageVars>({
    mutationFn: ({ moduleId, enrollmentId, query }) =>
      chatService.sendQuery(moduleId, enrollmentId, query),
  });
};
