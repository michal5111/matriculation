package pl.poznan.ue.matriculation.oracle.repo

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.SqlOutParameter
import org.springframework.jdbc.core.SqlParameter
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.simple.SimpleJdbcCall
import pl.poznan.ue.matriculation.annotation.LogExecutionTime
import pl.poznan.ue.matriculation.oracle.dto.IndexNumberDto
import java.sql.Types

open class IndexNumberRepositoryImpl(
    @Qualifier("oracleJdbcTemplate") val jdbcTemplate: JdbcTemplate
) : IndexNumberRepository {

    private val jdbcCall = SimpleJdbcCall(jdbcTemplate)
        .withSchemaName("USOS_PROD_TAB")
        .withCatalogName("pkg_immatrykulacja")
        .withProcedureName("nowy_indeks")
        .declareParameters(
            SqlParameter("p_typ", Types.VARCHAR),
            SqlOutParameter("p_numer", Types.VARCHAR),
            SqlOutParameter("p_jed_org_kod", Types.VARCHAR)
        )

    @LogExecutionTime
    override fun getNewIndexNumber(indexPoolCode: String): IndexNumberDto {
        val paramMap = MapSqlParameterSource().addValue("p_typ", indexPoolCode)
        val resultMap = jdbcCall.execute(paramMap)
        return IndexNumberDto(
            organizationalUnitCode = resultMap["p_jed_org_kod"] as String?,
            number = resultMap["p_numer"] as String
        )
    }
}
