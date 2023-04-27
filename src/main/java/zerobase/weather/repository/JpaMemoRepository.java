package zerobase.weather.repository;

import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface JpaMemoRepository extends JpaRepository<Memo, Integer>{
}
