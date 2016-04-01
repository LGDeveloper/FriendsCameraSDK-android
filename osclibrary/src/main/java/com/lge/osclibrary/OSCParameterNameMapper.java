/*
 * Copyright 2016 LG Electronics Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lge.osclibrary;

public class OSCParameterNameMapper {
    public static final String NAME = "name";
    public static final String PARAMETERS = "parameters";

    public static final String MAXSIZE = "maxSize";
    public static final String MAXTHUMBSIZE = "maxThumbSize";
    public static final String FILETYPE = "fileType";
    public static final String FILEURL = "fileUrl";
    public static final String FILEURLS = "fileUrls";

    public static final String RESULTS = "results";
    public static final String ENTRIES = "entries";

    public static final String ENTRYCOUNT = "entryCount";
    public static final String CONTINUATION_TOKEN = "continuationToken";

    public static final String COMMAND_STATE = "state";
    public static final String COMMAND_ID = "id";
    public static final String STATE_INPROGRESS = "inProgress";
    public static final String STATE_DONE = "done";

    public static final String FINGERPRINT = "fingerprint";
    public static final String LOCAL_FINGERPRINT = "stateFingerprint";
    public static final String TIMEOUT = "waitTimeout";


    public class FileInfo {
        public static final String NAME = "name";
        public static final String URL = "fileUrl";
        public static final String SIZE = "size";
    }

    public class Options {
        public static final String OPTIONS = "options";
        public static final String OPTIONNAMES = "optionNames";
    }

}
