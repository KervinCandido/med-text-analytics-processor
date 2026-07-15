const fs = require('fs');
const path = require('path');

const mapperDir = 'D:/workspace/med-text-analytics/med-text-analytics-processor/src/main/java/br/com/fiap/techchallenge/processor/persistence/mapper';

function walkSync(dir, filelist = []) {
  fs.readdirSync(dir).forEach(file => {
    const dirFile = path.join(dir, file);
    if (fs.statSync(dirFile).isDirectory()) {
      filelist = walkSync(dirFile, filelist);
    } else {
      filelist.push(dirFile);
    }
  });
  return filelist;
}

const mapperFiles = walkSync(mapperDir).filter(f => f.endsWith('.java'));

mapperFiles.forEach(file => {
  let content = fs.readFileSync(file, 'utf-8');
  
  // Extract package
  const pkgMatch = content.match(/package (.*?);/);
  const pkg = pkgMatch ? pkgMatch[1] : '';
  
  // Extract imports of Entity and Domain
  // Look for import ...entity...; and import ...domain...;
  const imports = [];
  const lines = content.split('\n');
  lines.forEach(line => {
      if (line.startsWith('import ') && !line.includes('com.fasterxml.jackson')) {
          imports.push(line.trim());
      }
  });
  
  // Class name
  const classNameMatch = content.match(/public class (\w+)/);
  if (!classNameMatch) return;
  const className = classNameMatch[1];
  
  // We know it's something like LaudoMapper. The domain is Laudo, entity is LaudoEntity.
  const baseName = className.replace('Mapper', '');
  
  const newContent = `package ${pkg};

${imports.join('\n')}
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface ${className} {
    ${baseName}Entity toEntity(${baseName} domain);
    ${baseName} toDomain(${baseName}Entity entity);
}
`;

  fs.writeFileSync(file, newContent);
});

console.log(`Rewrote ${mapperFiles.length} mappers to MapStruct interfaces.`);
