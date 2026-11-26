package com.example.mcmovilcare.data.remote

import com.example.mcmovilcare.model.Equipo
import com.example.mcmovilcare.model.Mantencion
import com.example.mcmovilcare.model.OrdenDeTrabajo
import com.example.mcmovilcare.model.SolicitudDeAtencion
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/login") suspend fun login(@Body body: LoginBody): LoginRespuesta
    @POST("api/usuarios") suspend fun registrar(@Body body: RegistroBody): UsuarioDto
    @GET("api/equipos") suspend fun equipos(): List<Equipo>
    @GET("api/ordenes") suspend fun ordenes(): List<OrdenDeTrabajo>
    @GET("api/mantenciones") suspend fun mantenciones(): List<Mantencion>
    @PATCH("api/ordenes/{id}/autorizacion")
    suspend fun responderAutorizacion(@Path("id") id: Int, @Body body: AutorizacionBody): OrdenDeTrabajo
    @GET("api/solicitudes") suspend fun solicitudes(): List<SolicitudDeAtencion>
    @POST("api/solicitudes") suspend fun crearSolicitud(@Body body: SolicitudBody): SolicitudDeAtencion
    @PUT("api/solicitudes/{id}")
    suspend fun editarSolicitud(@Path("id") id: Int, @Body body: SolicitudBody): SolicitudDeAtencion
    @PATCH("api/solicitudes/{id}/estado")
    suspend fun cambiarEstadoSolicitud(@Path("id") id: Int, @Body body: EstadoBody): SolicitudDeAtencion
    @DELETE("api/solicitudes/{id}") suspend fun eliminarSolicitud(@Path("id") id: Int): Response<Unit>
}