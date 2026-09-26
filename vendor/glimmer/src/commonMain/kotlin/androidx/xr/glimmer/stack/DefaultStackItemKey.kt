/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.xr.glimmer.stack

/**
 * The default stack item key wrapper that meets the following requirements:
 * 1) Objects created for the same index are equal.
 * 2) Objects created for different index values are never equal.
 * 3) Objects are not equal to any object that could be provided by a user as a custom key.
 * 4) Objects carry only their stable index.
 */
// CMP-PORT: Parcelable is Android-only. The private key needs equality and stable identity here;
// SaveableStateRegistry receives the surrounding stack state rather than this implementation type.
internal data class DefaultStackItemKey(private val index: Int)
