package compose.graphql.ast

import zio.parser.Syntax
import zio.Chunk

sealed trait Query {
  self =>
  def encode: String = ???
}

object Query {
  object Empty                                                                extends Query
  final case class Field(name: String, selection: Chunk[Field] = Chunk.empty) extends Query
  sealed trait Definition
  object Definition {
    final case class OperationDefinition(
      operation: Operation,
      name: Option[String],
      selectionSet: Chunk[Field],
    )
  }
  sealed trait Operation
  object Operation  {
    case object Query        extends Operation
    case object Mutation     extends Operation
    case object Subscription extends Operation
  }

  private lazy val emptySpace  = (Syntax.char('\n') | Syntax.char(' ')).repeat.unit(Chunk {})
  private lazy val emptySpace0 = (Syntax.char('\n') | Syntax.char(' ')).repeat0.unit(Chunk {})

  private lazy val anyName = Syntax.letter.repeat
    .transform[String](_.mkString, i => Chunk.fromArray(i.toCharArray()))

  def block[A](syntax: GraphQLSyntax[A]) = syntax.surroundedBy(emptySpace0)
    .between(Syntax.char('{'), Syntax.char('}'))

  private lazy val nestedField: GraphQLSyntax[Field] = { anyName ~ emptySpace ~ fieldSyntax }
    .transform[Field](
      { case (string, chunk) => Field(string, chunk) },
      { case field => (field.name, field.selection) },
    )

  private lazy val leafField: GraphQLSyntax[Field] = { anyName }
    .transform[Field]({ case (string) => Field(string, Chunk.empty) }, { case field => field.name })

  private lazy val fieldSyntax: GraphQLSyntax[Chunk[Field]] =
    block((nestedField | leafField).repeatWithSep0(emptySpace))

  lazy val querySyntax: GraphQLSyntax[Definition.OperationDefinition] = (Syntax
    .string("query", {}) ~ emptySpace ~ fieldSyntax).transform[Definition.OperationDefinition](
    { fields => Definition.OperationDefinition(Operation.Query, None, fields) },
    { operation => operation.selectionSet },
  )
}
