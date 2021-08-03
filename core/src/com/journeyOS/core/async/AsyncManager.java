/*
 * Copyright (c) 2021 anqi.huang@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.journeyOS.core.async;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

public class AsyncManager<JobResult> {

    private static Handler uiHandler = new Handler(Looper.getMainLooper());

    // Action to do in background
    private AsyncAction actionInBackground;
    // Action to do when the background action ends
    private AsyncResultAction mActionOnMainThread;

    // An optional ExecutorService to enqueue the actions
    private ExecutorService mExecutorService;

    private boolean bFinishedOnMainThread = true;

    // The thread created for the action
    private Thread asyncThread;
    // The FutureTask created for the action
    private FutureTask asyncFutureTask;

    // The result of the background action
    private JobResult result;

    /**
     * Instantiates a new AsyncManager
     */
    public AsyncManager() {
    }

    /**
     * Executes the provided code immediately on the UI Thread
     *
     * @param onMainThreadJob Interface that wraps the code to execute
     */
    public static void doOnMainThread(final OnMainThreadJob onMainThreadJob) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                onMainThreadJob.doInUIThread();
            }
        });
    }

    /**
     * Executes the provided code immediately on a background thread
     *
     * @param onBackgroundJob Interface that wraps the code to execute
     */
    public static void doInBackground(final OnBackgroundJob onBackgroundJob) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                onBackgroundJob.doOnBackground();
            }
        }).start();
    }

    /**
     * Executes the provided code immediately on a background thread that will be submitted to the
     * provided ExecutorService
     *
     * @param onBackgroundJob Interface that wraps the code to execute
     * @param executor        Will queue the provided code
     */
    public static FutureTask doInBackground(final OnBackgroundJob onBackgroundJob, ExecutorService executor) {
        FutureTask task = (FutureTask) executor.submit(new Runnable() {
            @Override
            public void run() {
                onBackgroundJob.doOnBackground();
            }
        });

        return task;
    }

    /**
     * Begins the background execution providing a result, similar to an AsyncTask.
     * It will execute it on a new Thread or using the provided ExecutorService
     */
    public void start() {
        if (actionInBackground != null) {

            Runnable jobToRun = new Runnable() {
                @Override
                public void run() {
                    result = (JobResult) actionInBackground.doAsync();
                    onResult();
                }
            };

            if (getExecutorService() != null) {
                asyncFutureTask = (FutureTask) getExecutorService().submit(jobToRun);
            } else {
                asyncThread = new Thread(jobToRun);
                asyncThread.start();
            }
        }
    }

    /**
     * Cancels the AsyncManager interrupting the inner thread.
     */
    public void cancel() {
        if (actionInBackground != null) {
            if (mExecutorService != null) {
                asyncFutureTask.cancel(true);
            } else {
                asyncThread.interrupt();
            }
        }
    }


    private void onResult() {
        if (mActionOnMainThread != null) {
            if (bFinishedOnMainThread) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mActionOnMainThread.onResult(result);
                    }
                });
            } else {
                mActionOnMainThread.onResult(result);
            }
        }
    }

    public ExecutorService getExecutorService() {
        return mExecutorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.mExecutorService = executorService;
    }

    public void setFinishedOnMainThread(boolean finishedOnMainThread) {
        this.bFinishedOnMainThread = finishedOnMainThread;
    }

    public AsyncAction getActionInBackground() {
        return actionInBackground;
    }

    /**
     * Specifies which action to run in background
     *
     * @param actionInBackground the action
     */
    public void setActionInBackground(AsyncAction actionInBackground) {
        this.actionInBackground = actionInBackground;
    }

    public AsyncResultAction getActionOnResult() {
        return mActionOnMainThread;
    }

    /**
     * Specifies which action to run when the background action is finished
     *
     * @param actionOnMainThread the action
     */
    public void setActionOnResult(AsyncResultAction actionOnMainThread) {
        this.mActionOnMainThread = actionOnMainThread;
    }

    public interface AsyncAction<ActionResult> {
        ActionResult doAsync();
    }

    public interface AsyncResultAction<ActionResult> {
        void onResult(ActionResult result);
    }

    public interface OnMainThreadJob {
        void doInUIThread();
    }

    public interface OnBackgroundJob {
        void doOnBackground();
    }

    /**
     * Builder class to instantiate an AsyncManager in a clean way
     *
     * @param <JobResult> the type of the expected result
     */
    public static class AsyncJobBuilder<JobResult> {

        private AsyncAction<JobResult> asyncAction;
        private AsyncResultAction asyncResultAction;
        private ExecutorService executor = null;
        private boolean bFinishedOnMainThread = false;

        public AsyncJobBuilder() {

        }

        /**
         * Specifies which action to run on background
         *
         * @param action the AsyncAction to run
         * @return the builder object
         */
        public AsyncJobBuilder<JobResult> doInBackground(AsyncAction<JobResult> action) {
            asyncAction = action;
            return this;
        }

        /**
         * Specifies which action to run when the background action ends
         *
         * @param action the AsyncAction to run
         * @return the builder object
         */
        public AsyncJobBuilder<JobResult> doWhenFinished(AsyncResultAction action) {
            return doWhenFinished(action, true);
        }

        public AsyncJobBuilder<JobResult> doWhenFinished(AsyncResultAction action, boolean finishedOnMainThread) {
            asyncResultAction = action;
            bFinishedOnMainThread = finishedOnMainThread;
            return this;
        }

        /**
         * Used to provide an ExecutorService to launch the AsyncActions
         *
         * @param executor the ExecutorService which will queue the actions
         * @return the builder object
         */
        public AsyncJobBuilder<JobResult> withExecutor(ExecutorService executor) {
            this.executor = executor;
            return this;
        }

        /**
         * Instantiates a new AsyncManager of the given type
         *
         * @return a configured AsyncManager instance
         */
        public AsyncManager<JobResult> create() {
            AsyncManager<JobResult> asyncManager = new AsyncManager<JobResult>();
            asyncManager.setActionInBackground(asyncAction);
            asyncManager.setActionOnResult(asyncResultAction);
            asyncManager.setExecutorService(executor);
            asyncManager.setFinishedOnMainThread(bFinishedOnMainThread);
            return asyncManager;
        }


    }

}
