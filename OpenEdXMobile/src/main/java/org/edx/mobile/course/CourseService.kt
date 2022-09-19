package org.edx.mobile.course

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.ResponseBody
import org.edx.mobile.http.provider.RetrofitProvider
import org.edx.mobile.model.Page
import org.edx.mobile.model.api.CourseComponentStatusResponse
import org.edx.mobile.model.api.CourseUpgradeResponse
import org.edx.mobile.model.api.EnrollmentResponse
import org.edx.mobile.model.course.BlocksCompletionBody
import org.edx.mobile.model.course.CourseBannerInfoModel
import org.edx.mobile.model.course.CourseDates
import org.edx.mobile.model.course.CourseDetail
import org.edx.mobile.model.course.CourseStatus
import org.edx.mobile.model.course.CourseStructureV1Model
import org.edx.mobile.model.course.EnrollBody
import org.edx.mobile.model.course.ResetCourseDates
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Singleton

interface CourseService {
    /**
     * A Provider implementation for CourseService.
     */
    @Module
    @InstallIn(SingletonComponent::class)
    class Provider {
        @Singleton
        @Provides
        operator fun get(retrofitProvider: RetrofitProvider): CourseService {
            return retrofitProvider.withOfflineCache.create(CourseService::class.java)
        }
    }

    /**
     * @return Enrolled courses of given user.
     */
    @GET("/api/mobile/{api_version}/users/{username}/course_enrollments")
    fun getEnrolledCourses(
        @Header("Cache-Control") cacheControlHeaderParam: String?,
        @Path("username") username: String,
        @Path("api_version") enrollmentsApiVersion: String,
        @Query("org") org: String?
    ): Call<EnrollmentResponse>

    /**
     * @return Enrolled courses of given user, only from the cache.
     */
    @Headers("Cache-Control: only-if-cached, max-stale")
    @GET("/api/mobile/{api_version}/users/{username}/course_enrollments")
    fun getEnrolledCoursesFromCache(
        @Path("username") username: String,
        @Path("api_version") enrollmentsApiVersion: String,
        @Query("org") org: String?
    ): Call<EnrollmentResponse>

    @POST("/api/enrollment/v1/enrollment")
    fun enrollInACourse(@Body body: EnrollBody): Call<ResponseBody>

    /**
     * @param username (optional) The username of the specified user whose visible courses we
     *      want to see. The username is not required only if the API is requested by an Anonymous
     *      user.
     * @param mobile (optional): If specified, only visible `CourseOverview` objects that are
     *      designated as mobile_available are returned.
     * @param org (optional): If specified, only courses with the relevant organization
     *      code are returned.
     * @param page (optional): Which page to fetch. If not given, defaults to page 1
     */
    @GET("/api/courses/v1/courses/")
    fun getCourseList(
        @Query("username") username: String?,
        @Query("mobile") mobile: Boolean,
        @Query("org") org: String?,
        @Query("page") page: Int
    ): Call<Page<CourseDetail>>

    /**
     * @param courseId (optional): If specified, visible `CourseOverview` objects are filtered
     *      such that only those belonging to the organization with the provided org code (e.g.,
     *      "HarvardX") are returned.
     *      Case-insensitive.
     * @param username (optional): The username of the specified user whose visible courses we
     *      want to see. The username is not required only if the API is requested by an Anonymous user.
     */
    @GET("/api/courses/v1/courses/{course_id}")
    fun getCourseDetail(
        @Path("course_id") courseId: String,
        @Query("username") username: String
    ): Call<CourseDetail>

    @GET(
        "/api/courses/{api_version}/blocks/?" +
                "depth=all&" +
                "requested_fields=contains_gated_content,show_gated_sections,special_exam_info,graded,format,student_view_multi_device,due,completion&" +
                "student_view_data=video,discussion&" +
                "block_counts=video&" +
                "nav_depth=3"
    )
    fun getCourseStructure(
        @Header("Cache-Control") cacheControlHeaderParam: String,
        @Path("api_version") blocksApiVersion: String,
        @Query("username") username: String,
        @Query("course_id") courseId: String
    ): Call<CourseStructureV1Model>

    @GET("/api/course_home/v1/dates/{course_key}")
    fun getCourseDates(@Path("course_key") courseId: String): Call<CourseDates>

    @POST("/api/course_experience/v1/reset_course_deadlines")
    fun resetCourseDates(@Body courseBody: Map<String, String>): Call<ResetCourseDates>

    @GET("/api/courseware/course/{course_id}")
    fun getCourseStatus(@Path("course_id") courseId: String): Call<CourseStatus>

    @GET("/api/mobile/v1/users/{username}/course_status_info/{course_id}")
    fun getCourseStatusInfo(
        @Path("username") username: String,
        @Path("course_id") courseId: String
    ): Call<CourseComponentStatusResponse>

    /**
     * @return Upgrade status for the given course.
     */
    @Headers("Cache-Control: no-cache")
    @GET("/api/experiments/v0/custom/REV-934")
    fun getCourseUpgradeStatus(@Query("course_id") courseId: String): Call<CourseUpgradeResponse>

    @GET("/api/course_experience/v1/course_deadlines_info/{course_key}")
    fun getCourseBannerInfo(@Path("course_key") courseId: String): Call<CourseBannerInfoModel>

    @POST("/api/completion/v1/completion-batch")
    fun markBlocksCompletion(@Body completionBody: BlocksCompletionBody?): Call<JSONObject>

    @POST("/api/courseware/celebration/{course_id}")
    fun updateCoursewareCelebration(
        @Path("course_id") courseId: String,
        @Body courseBody: Map<String, Boolean>
    ): Call<Void>
}
